package com.studyhub.service;

import com.studyhub.model.Usuario;
import com.studyhub.model.Asignatura;
import com.studyhub.dto.EstadisticasDTO;
import com.studyhub.repository.UsuarioRepository;
import com.studyhub.repository.AsignaturaRepository;
import com.studyhub.service.NotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AsignaturaRepository asignaturaRepository;

    @Autowired
    private NotaService notaService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Usuario crearUsuario(Usuario usuario) {

        // Validar correo único
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // Cifrar contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        return usuarioRepository.save(usuario);
    }

    public EstadisticasDTO obtenerEstadisticas(Long usuarioId) {
        List<Asignatura> asignaturas = asignaturaRepository.findAll();
        
        int totalMaterias = asignaturas.size();
        int totalCreditos = 0;
        int materiasEnRiesgo = 0;
        double sumaPromedios = 0.0;
        Map<String, Double> promediosPorMateria = new HashMap<>();

        for (Asignatura asig : asignaturas) {
            double promedio = notaService.calcularPromedio(asig.getId());
            promediosPorMateria.put(asig.getNombre(), promedio);
            
            sumaPromedios += promedio;
            totalCreditos += asig.getCreditos();
            
            if (promedio < 3.0 && promedio > 0) {
                materiasEnRiesgo++;
            }
        }

        double promedioGlobal = totalMaterias > 0 ? (sumaPromedios / totalMaterias) : 0.0;

        return new EstadisticasDTO(
            Math.round(promedioGlobal * 100.0) / 100.0,
            totalMaterias,
            materiasEnRiesgo,
            totalCreditos,
            promediosPorMateria
        );
    }
}