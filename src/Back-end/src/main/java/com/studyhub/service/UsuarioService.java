package com.studyhub.service;

import com.studyhub.dto.AsignaturaResumenDTO;
import com.studyhub.dto.ResumenAcademicoDTO;
import com.studyhub.dto.TareaResumenDTO;
import com.studyhub.dto.UsuarioResumenDTO;
import com.studyhub.dto.EstadisticasDTO;
import com.studyhub.model.Asignatura;
import com.studyhub.model.Tarea;
import com.studyhub.model.Usuario;
import com.studyhub.repository.*;
import com.studyhub.service.strategy.PasswordEncryptionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ResenaRepository resenaRepository;

    @Autowired
    private AsignacionRepository asignacionRepository;

    @Autowired
    private ApunteRepository apunteRepository;

    @Autowired
    private NotaRepository notaRepository;

    @Autowired
    private AsignaturaRepository asignaturaRepository;

    @Autowired
    private SesionInvalidadaRepository sesionInvalidadaRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    private final AsignaturaService asignaturaService;
    private final NotaService notaService;
    private final TareaRepository tareaRepository;
    private final PasswordEncryptionStrategy encryptionStrategy;
    private final ObjectMapper objectMapper;

    @Autowired
    public UsuarioService(PasswordEncryptionStrategy encryptionStrategy,
            AsignaturaService asignaturaService,
            NotaService notaService,
            TareaRepository tareaRepository,
            ObjectMapper objectMapper) {
        this.encryptionStrategy = encryptionStrategy;
        this.asignaturaService = asignaturaService;
        this.notaService = notaService;
        this.tareaRepository = tareaRepository;
        this.objectMapper = objectMapper;
    }

    public String normalizarTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return null;
        }
        String limpio = telefono.trim().replaceAll("[^0-9+]", "");
        if (!limpio.startsWith("+57")) {
            if (limpio.startsWith("57") && limpio.length() == 12) {
                limpio = "+" + limpio;
            } else if (limpio.startsWith("3") && limpio.length() == 10) {
                limpio = "+57" + limpio;
            } else {
                limpio = "+57" + limpio;
            }
        }
        return limpio;
    }

    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }
        if (usuario.getTelefono() != null && !usuario.getTelefono().trim().isEmpty()) {
            String telNorm = normalizarTelefono(usuario.getTelefono());
            if (usuarioRepository.existsByTelefono(telNorm)) {
                throw new RuntimeException("El teléfono ya está registrado");
            }
            usuario.setTelefono(telNorm);
        }
        usuario.setPassword(encryptionStrategy.encrypt(usuario.getPassword()));
        return usuarioRepository.saveAndFlush(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario login(String correo, String password) {
        return usuarioRepository.findByCorreo(correo)
                .filter(u -> encryptionStrategy.matches(password, u.getPassword()))
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
    }

    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Busca un usuario por su ID y lo retorna.
     * Se usa para precargar el formulario "Mi Perfil" en el frontend.
     *
     * @param id ID del usuario
     * @return El usuario encontrado
     * @throws RuntimeException si no existe un usuario con ese ID (→ 404)
     */
    @Transactional(readOnly = true)
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }



    /**
     * Actualiza los campos de perfil editables de un usuario:
     * nombre, apellido, carrera y semestre.
     *
     * Validaciones:
     * - El usuario debe existir (404 si no).
     * - nombre y apellido son obligatorios y no pueden estar vacíos (400).
     * - carrera y semestre son opcionales.
     *
     * @param id     ID del usuario a actualizar
     * @param campos Mapa con los campos: nombre, apellido, carrera, semestre
     * @return El usuario actualizado y persistido
     * @throws RuntimeException con mensaje descriptivo si falla la validación
     */
    public Usuario actualizarPerfil(Long id, Map<String, Object> campos) {
        // Buscar usuario — lanza excepción si no existe (se convierte en 404)
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Validar nombre (obligatorio)
        String nombre = campos.get("nombre") != null ? campos.get("nombre").toString().trim() : "";
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        // Validar apellido (obligatorio)
        String apellido = campos.get("apellido") != null ? campos.get("apellido").toString().trim() : "";
        if (apellido.isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }

        // Aplicar cambios obligatorios
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);

        // Aplicar campos opcionales si vienen en el body
        if (campos.containsKey("carrera")) {
            String carrera = campos.get("carrera") != null ? campos.get("carrera").toString().trim() : null;
            usuario.setCarrera(carrera.isEmpty() ? null : carrera);
        }

        if (campos.containsKey("semestre")) {
            try {
                Integer semestre = campos.get("semestre") != null
                        ? Integer.parseInt(campos.get("semestre").toString())
                        : null;
                if (semestre != null && (semestre < 1 || semestre > 12)) {
                    throw new IllegalArgumentException("El semestre debe estar entre 1 y 12");
                }
                usuario.setSemestre(semestre);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("El semestre debe ser un número entero válido");
            }
        }

        if (campos.containsKey("temaColor")) {
            String temaColor = campos.get("temaColor") != null ? campos.get("temaColor").toString().trim() : null;
            usuario.setTemaColor(temaColor == null || temaColor.isEmpty() ? null : temaColor);
        }

        if (campos.containsKey("fotoPerfil")) {
            String fotoPerfil = campos.get("fotoPerfil") != null ? campos.get("fotoPerfil").toString().trim() : null;
            usuario.setFotoPerfil(fotoPerfil == null || fotoPerfil.isEmpty() ? null : fotoPerfil);
        }

        if (campos.containsKey("telefono")) {
            String telRaw = campos.get("telefono") != null ? campos.get("telefono").toString().trim() : null;
            String telNorm = normalizarTelefono(telRaw);
            if (telNorm != null && !telNorm.equals(usuario.getTelefono())) {
                if (usuarioRepository.existsByTelefono(telNorm)) {
                    throw new RuntimeException("El teléfono ya está registrado en otra cuenta");
                }
            }
            usuario.setTelefono(telNorm);
        }

        return usuarioRepository.saveAndFlush(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResumenDTO obtenerResumenUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        List<Asignatura> asignaturas = asignaturaService.findByUserId(id);
        int totalAsignaturas = asignaturas.size();

        double sumaPromedios = 0.0;
        for (Asignatura asignatura : asignaturas) {
            double promedioAsignatura = notaService.calcularPromedio(asignatura.getId());
            sumaPromedios += promedioAsignatura;
        }

        double promedioGlobal = 0.0;
        if (totalAsignaturas > 0) {
            promedioGlobal = sumaPromedios / totalAsignaturas;
        }

        promedioGlobal = Math.round(promedioGlobal * 100.0) / 100.0;

        return new UsuarioResumenDTO(
                usuario.getId(),
                usuario.getNombre() + " " + (usuario.getApellido() != null ? usuario.getApellido() : ""),
                totalAsignaturas,
                promedioGlobal);
    }

    @Transactional(readOnly = true)
    public ResumenAcademicoDTO obtenerResumenAcademico(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Asignatura> asignaturasRaw = asignaturaService.findByUserId(usuarioId);
        List<AsignaturaResumenDTO> asignaturasResumen = new ArrayList<>();
        double sumaPromedios = 0;

        for (Asignatura asig : asignaturasRaw) {
            double promedio = notaService.calcularPromedio(asig.getId());
            sumaPromedios += promedio;
            asignaturasResumen.add(new AsignaturaResumenDTO(
                    asig.getNombre(),
                    notaService.obtenerNotasPorAsignatura(asig.getId()),
                    promedio,
                    promedio < 3.0));
        }

        double promedioGlobal = asignaturasRaw.isEmpty() ? 0 : sumaPromedios / asignaturasRaw.size();
        promedioGlobal = Math.round(promedioGlobal * 100.0) / 100.0;

        List<Tarea> tareasRaw = tareaRepository
                .findByAsignatura_Usuario_IdAndEstadoTrueOrderByFechaEntregaAsc(usuarioId);
        List<TareaResumenDTO> tareasResumen = tareasRaw.stream()
                .map(t -> new TareaResumenDTO(t.getTitulo(), t.getAsignatura().getNombre(), t.getFechaEntrega(),
                        t.getHoraEntrega()))
                .collect(Collectors.toList());

        return new ResumenAcademicoDTO(
                usuario.getNombre() + " " + (usuario.getApellido() != null ? usuario.getApellido() : ""),
                promedioGlobal,
                asignaturasResumen,
                tareasResumen);
    }

    public String generarTokenRecuperacion(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("No existe un usuario registrado con ese correo"));

        String token = UUID.randomUUID().toString();
        usuario.setTokenRecuperacion(token);
        usuario.setTokenExpiracion(LocalDateTime.now().plusHours(1));
        usuarioRepository.saveAndFlush(usuario);
        return token;
    }

    public String generarTokenRecuperacionPorTelefono(String telefonoRaw) {
        String telefono = normalizarTelefono(telefonoRaw);
        Usuario usuario = usuarioRepository.findByTelefono(telefono)
                .orElseThrow(() -> new RuntimeException("No existe un usuario registrado con ese número celular"));

        String token = UUID.randomUUID().toString();
        usuario.setTokenRecuperacion(token);
        usuario.setTokenExpiracion(LocalDateTime.now().plusHours(1));
        usuarioRepository.saveAndFlush(usuario);
        return token;
    }

    public void restablecerPassword(String token, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findByTokenRecuperacion(token)
                .orElseThrow(() -> new RuntimeException("Token de recuperación inválido"));

        if (usuario.getTokenExpiracion().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El token ha expirado");
        }

        usuario.setPassword(encryptionStrategy.encrypt(nuevaPassword));
        usuario.setTokenRecuperacion(null);
        usuario.setTokenExpiracion(null);
        usuarioRepository.saveAndFlush(usuario);
    }

    @Transactional(readOnly = true)
    public EstadisticasDTO obtenerEstadisticas(Long usuarioId) {
        List<Asignatura> asignaturas = asignaturaService.findByUserId(usuarioId);

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
                promediosPorMateria);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerPreferencias(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        String prefs = usuario.getPreferencias();
        if (prefs == null || prefs.trim().isEmpty()) {
            return new HashMap<>(); // Preferencias por defecto
        }

        try {
            return objectMapper.readValue(prefs, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            System.err.println("Error al parsear preferencias JSON: " + e.getMessage());
            return new HashMap<>();
        }
    }

    public void guardarPreferencias(Long usuarioId, Map<String, Object> preferencias) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        try {
            String prefsJson = objectMapper.writeValueAsString(preferencias);
            usuario.setPreferencias(prefsJson);
            usuarioRepository.saveAndFlush(usuario);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al serializar preferencias JSON", e);
        }
    }

    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // 1. Sesiones invalidadas
        sesionInvalidadaRepository.deleteByUsuarioId(id);

        // 2. Tokens de reseteo de contraseña
        passwordResetTokenRepository.deleteByUsuario(usuario);

        // 3. Notificaciones del usuario
        notificationRepository.deleteAll(notificationRepository.findByUserIdOrderByCreatedAtDesc(id));

        // 4. Reseñas del usuario
        resenaRepository.deleteAll(resenaRepository.findByUsuarioId(id));

        // 5. Asignaciones de turnos del usuario
        asignacionRepository.deleteAll(asignacionRepository.findByUsuarioId(id));

        // 6. Tareas, notas, apuntes y asignaturas
        List<Asignatura> asignaturas = asignaturaRepository.findByUsuarioId(id);
        for (Asignatura asig : asignaturas) {
            tareaRepository.deleteAll(tareaRepository.findByAsignatura_Usuario_Id(id));
            notaRepository.deleteAll(notaRepository.findByAsignatura_Usuario_Id(id));
            apunteRepository.deleteAll(apunteRepository.findByAsignatura_Usuario_Id(id));
            asignaturaRepository.delete(asig);
        }

        // 7. Borrar usuario
        usuarioRepository.delete(usuario);
        usuarioRepository.flush();
    }
}