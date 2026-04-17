package com.studyhub.service;

import com.studyhub.dto.AsignaturaResumenDTO;
import com.studyhub.dto.ResumenAcademicoDTO;
import com.studyhub.dto.TareaResumenDTO;
import com.studyhub.dto.UsuarioResumenDTO;
import com.studyhub.model.Asignatura;
import com.studyhub.model.Tarea;
import com.studyhub.model.Usuario;
import com.studyhub.repository.TareaRepository;
import com.studyhub.repository.UsuarioRepository;
import com.studyhub.service.strategy.PasswordEncryptionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final AsignaturaService asignaturaService;
    private final NotaService notaService;
    private final TareaRepository tareaRepository;

    // CORREGIDO: PasswordEncryptionStrategy ahora existe (ver BCryptEncryptionStrategy)
    private final PasswordEncryptionStrategy encryptionStrategy;

    @Autowired
    public UsuarioService(PasswordEncryptionStrategy encryptionStrategy, 
                          AsignaturaService asignaturaService, 
                          NotaService notaService,
                          TareaRepository tareaRepository) {
        this.encryptionStrategy = encryptionStrategy;
        this.asignaturaService = asignaturaService;
        this.notaService = notaService;
        this.tareaRepository = tareaRepository;
    }

    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }
        usuario.setPassword(encryptionStrategy.encrypt(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public Usuario login(String correo, String password) {
        return usuarioRepository.findByCorreo(correo)
                .filter(u -> encryptionStrategy.matches(password, u.getPassword()))
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
    }

    // NUEVO: método requerido por UsuarioController
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public UsuarioResumenDTO obtenerResumenUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Obtener asignaturas registradas
        List<Asignatura> asignaturas = asignaturaService.findByUserId(id);
        int totalAsignaturas = asignaturas.size();

        double sumaPromedios = 0.0;
        
        // El promedio ponderado global será el promedio simple de los promedios individuales por requerimiento.
        for (Asignatura asignatura : asignaturas) {
            double promedioAsignatura = notaService.calcularPromedio(asignatura.getId());
            sumaPromedios += promedioAsignatura;
        }

        double promedioGlobal = 0.0;
        if (totalAsignaturas > 0) {
            promedioGlobal = sumaPromedios / totalAsignaturas;
        }
        
        // Redondear a 2 decimales
        promedioGlobal = Math.round(promedioGlobal * 100.0) / 100.0;

        return new UsuarioResumenDTO(
                usuario.getId(),
                usuario.getNombre() + " " + (usuario.getApellido() != null ? usuario.getApellido() : ""),
                totalAsignaturas,
                promedioGlobal
        );
    }

    public ResumenAcademicoDTO obtenerResumenAcademico(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 1. Obtener asignaturas con sus notas y promedios
        List<Asignatura> asignaturasRaw = asignaturaService.findByUserId(usuarioId);
        List<AsignaturaResumenDTO> asignaturasResumen = new ArrayList<>();
        double sumaPromedios = 0;

        for (Asignatura asig : asignaturasRaw) {
            double promedio = notaService.calcularPromedio(asig.getId());
            sumaPromedios += promedio;
            asignaturasResumen.add(new AsignaturaResumenDTO(
                asig.getNombre(),
                notaService.obtenerNotasPorAsignatura(asig.getId()),
                promedio
            ));
        }

        double promedioGlobal = asignaturasRaw.isEmpty() ? 0 : sumaPromedios / asignaturasRaw.size();
        promedioGlobal = Math.round(promedioGlobal * 100.0) / 100.0;

        // 2. Obtener tareas pendientes
        List<Tarea> tareasRaw = tareaRepository.findByAsignatura_Usuario_IdAndEstadoTrueOrderByFechaEntregaAsc(usuarioId);
        List<TareaResumenDTO> tareasResumen = tareasRaw.stream()
            .map(t -> new TareaResumenDTO(t.getTitulo(), t.getAsignatura().getNombre(), t.getFechaEntrega()))
            .collect(Collectors.toList());

        return new ResumenAcademicoDTO(
            usuario.getNombre() + " " + (usuario.getApellido() != null ? usuario.getApellido() : ""),
            promedioGlobal,
            asignaturasResumen,
            tareasResumen
        );
    }
}
