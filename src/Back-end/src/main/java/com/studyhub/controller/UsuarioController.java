package com.studyhub.controller;

import com.studyhub.model.Usuario;
import com.studyhub.dto.*;
import com.studyhub.model.Asignatura;
import com.studyhub.service.AsignaturaService;
import com.studyhub.service.NotaService;
import com.studyhub.service.UsuarioService;
import com.studyhub.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private com.studyhub.service.SmsService smsService;

    @Autowired
    private AsignaturaService asignaturaService;

    @Autowired
    private NotaService notaService;

    @Autowired
    private com.studyhub.service.AuthService authService;

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            Usuario creado = usuarioService.crearUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        try {
            String correo   = credenciales.get("correo");
            String password = credenciales.get("password");
            Usuario usuario = usuarioService.login(correo, password);
            // Limpiar registros de logout anteriores para que el guard de sesión
            // no invalide inmediatamente la sesión recién iniciada.
            authService.limpiarSesionesAnteriores(usuario.getId());
            String loginAt = java.time.LocalDateTime.now().toString();
            if (usuario.getTelefono() != null && !usuario.getTelefono().trim().isEmpty()) {
                smsService.enviarAlertaLogin(usuario.getTelefono().trim(), usuario.getNombre(), loginAt);
            }
            return ResponseEntity.ok(Map.of(
                "id",       usuario.getId(),
                "nombre",   usuario.getNombre(),
                "apellido", usuario.getApellido(),
                "correo",   usuario.getCorreo(),
                "rol",      usuario.getRol(),
                "loginAt",  loginAt          // timestamp del login para el guard
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping
    public List<Usuario> obtenerUsuarios() {
        return usuarioService.obtenerTodos();
    }

    /**
     * Retorna los datos públicos de perfil de un usuario por su ID.
     * Se usa desde el frontend para precargar el formulario "Mi Perfil".
     *
     * Respuestas:
     *  - 200 OK       → datos del usuario
     *  - 404 Not Found → no existe un usuario con ese ID
     *
     * @param id ID del usuario
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Usuario u = usuarioService.obtenerPorId(id);
            return ResponseEntity.ok(u);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/{id}/resumen")
    public ResponseEntity<UsuarioResumenDTO> obtenerResumenUsuario(@PathVariable Long id) {
        try {
            UsuarioResumenDTO resumen = usuarioService.obtenerResumenUsuario(id);
            return ResponseEntity.ok(resumen);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}/informe-academico")
    public ResponseEntity<com.studyhub.dto.ResumenAcademicoDTO> obtenerInformeAcademico(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioService.obtenerResumenAcademico(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Actualiza los campos de perfil editables de un usuario.
     *
     * Campos aceptados en el body:
     *  - nombre    (String, obligatorio)
     *  - apellido  (String, obligatorio)
     *  - carrera   (String, opcional)
     *  - semestre  (Integer 1-12, opcional)
     *
     * Respuestas:
     *  - 200 OK           → usuario actualizado correctamente
     *  - 400 Bad Request  → campos obligatorios vacíos o semestre inválido
     *  - 404 Not Found    → no existe un usuario con ese ID
     *
     * @param id     ID del usuario a actualizar
     * @param campos Mapa con los campos del perfil
     * @return El usuario actualizado o un mensaje de error
     */
    @PutMapping("/{id}/perfil")
    public ResponseEntity<?> actualizarPerfil(@PathVariable Long id,
                                              @RequestBody Map<String, Object> campos) {
        try {
            Usuario actualizado = usuarioService.actualizarPerfil(id, campos);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Perfil actualizado exitosamente",
                    "usuario", actualizado
            ));
        } catch (IllegalArgumentException e) {
            // Validación fallida → 400
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        } catch (RuntimeException e) {
            // Usuario no encontrado → 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }


    @PostMapping("/recuperar")
    public ResponseEntity<?> solicitarRecuperacion(@RequestParam String telefono) {
        try {
            String token = usuarioService.generarTokenRecuperacionPorTelefono(telefono);
            String baseUri = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            String enlace = baseUri + "/index.html?token=" + token;
            smsService.enviarSmsRecuperacion(telefono, enlace);
            return ResponseEntity.ok(Map.of("mensaje", "Enlace seguro enviado por SMS exitosamente al " + telefono));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PostMapping("/restablecer")
    public ResponseEntity<?> restablecerPassword(@RequestBody RestablecerRequest request) {
        try {
            usuarioService.restablecerPassword(request.getToken(), request.getNuevaPassword());
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<EstadisticasDTO> obtenerEstadisticas(@PathVariable Long id) {
        EstadisticasDTO estadisticas = usuarioService.obtenerEstadisticas(id);
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/{id}/preferencias")
    public ResponseEntity<?> obtenerPreferencias(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioService.obtenerPreferencias(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PutMapping("/{id}/preferencias")
    public ResponseEntity<?> guardarPreferencias(@PathVariable Long id, @RequestBody Map<String, Object> preferencias) {
        try {
            usuarioService.guardarPreferencias(id, preferencias);
            return ResponseEntity.ok(Map.of("mensaje", "Preferencias guardadas exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
        }
    }

    /**
     * Retorna la lista de materias del usuario con los campos requeridos por la HU:
     * id, name, description, status, redirectUrl e iconUrl.
     *
     * El status se deriva del progreso de calificaciones registradas:
     *   - pendiente    → 0% evaluado (ninguna nota con calificación)
     *   - en_progreso  → entre 1% y 99% evaluado
     *   - completada   → 100% evaluado
     *
     * redirectUrl apunta al módulo de calificaciones de cada materia.
     * iconUrl queda null por ahora — el modelo no tiene campo de ícono.
     *
     * Solo retorna materias del usuario indicado en el path.
     * Si el usuario no existe responde 404.
     *
     * @param id ID del usuario autenticado
     * @return Lista de SubjectSummaryDTO o error
     */
    @GetMapping("/{id}/subjects")
    public ResponseEntity<?> obtenerMateriasPorUsuario(@PathVariable Long id) {
        try {
            // Verificar que el usuario existe — restricción por usuario autenticado
            usuarioService.obtenerPorId(id);

            List<Asignatura> asignaturas = asignaturaService.findByUserId(id);

            List<SubjectSummaryDTO> subjects = asignaturas.stream().map(a -> {
                // Calcular progreso para derivar el status
                double progreso = notaService.calcularProgreso(a.getId());

                String status;
                if (progreso <= 0.0) {
                    status = "pendiente";
                } else if (progreso >= 100.0) {
                    status = "completada";
                } else {
                    status = "en_progreso";
                }

                // description combina profesor y período
                String description = String.format("%s · %s · %d créditos",
                        a.getProfesor(), a.getPeriodo(), a.getCreditos());

                // redirectUrl: ruta relativa al módulo de calificaciones de la materia
                String redirectUrl = "/materias/" + a.getId() + "/calificaciones";

                return new SubjectSummaryDTO(
                        a.getId(),
                        a.getNombre(),
                        description,
                        status,
                        redirectUrl,
                        null   // iconUrl: sin sistema de íconos por ahora
                );
            }).collect(java.util.stream.Collectors.toList());

            return ResponseEntity.ok(subjects);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }
}