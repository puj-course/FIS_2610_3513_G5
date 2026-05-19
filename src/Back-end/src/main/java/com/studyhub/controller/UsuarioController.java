package com.studyhub.controller;

import com.studyhub.model.Usuario;
import com.studyhub.dto.*;
import com.studyhub.model.Asignatura;
import com.studyhub.service.AsignaturaService;
import com.studyhub.service.IUsuarioService;
import com.studyhub.service.NotaService;
import com.studyhub.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    @Qualifier("usuarioServiceProxy")
    private IUsuarioService usuarioService;

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

    // ─────────────────────────────────────────────────────────────────────────
    // REGISTRO
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * POST /api/usuarios
     * Registra un nuevo usuario. Si tiene teléfono, envía un código OTP
     * vía Twilio Verify para verificar su número celular.
     */
    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            Usuario creado = usuarioService.crearUsuario(usuario);

            // Si el usuario registró número de teléfono, enviar OTP de verificación
            if (creado.getTelefono() != null && !creado.getTelefono().trim().isEmpty()) {
                boolean enviado = smsService.enviarCodigoVerificacion(creado.getTelefono());
                System.out.println("📲 [REGISTRO] OTP enviado a " + creado.getTelefono() + ": " + enviado);
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "usuario", creado,
                    "otpEnviado", enviado,
                    "mensaje", enviado
                        ? "Usuario creado. Se envió un código de verificación por SMS al " + creado.getTelefono()
                        : "Usuario creado. No se pudo enviar el SMS de verificación."
                ));
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // VERIFICACIÓN DE NÚMERO (OTP al registrarse)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * POST /api/usuarios/verificar-telefono
     * Verifica el código OTP enviado al registrarse.
     *
     * Body: { "telefono": "+573142733826", "codigo": "123456" }
     */
    @PostMapping("/verificar-telefono")
    public ResponseEntity<?> verificarTelefono(@RequestBody Map<String, String> body) {
        String telefono = body.get("telefono");
        String codigo   = body.get("codigo");

        if (telefono == null || codigo == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "Se requieren los campos 'telefono' y 'codigo'"));
        }

        boolean valido = smsService.verificarCodigo(telefono, codigo);

        if (valido) {
            return ResponseEntity.ok(Map.of(
                "verificado", true,
                "mensaje", "Número de teléfono verificado exitosamente"
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "verificado", false,
                "mensaje", "Código incorrecto o expirado"
            ));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LOGIN
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        try {
            String correo   = credenciales.get("correo");
            String password = credenciales.get("password");
            Usuario usuario = usuarioService.login(correo, password);

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
                "loginAt",  loginAt
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RECUPERACIÓN DE CONTRASEÑA VÍA SMS (OTP)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * POST /api/usuarios/recuperar?telefono=+573142733826
     * Envía un código OTP de 6 dígitos al número registrado para
     * recuperar la contraseña. El código es generado por Twilio Verify.
     */
    @PostMapping("/recuperar")
    public ResponseEntity<?> solicitarRecuperacion(@RequestParam String telefono) {
        try {
            // Verifica que el teléfono esté registrado en el sistema
            String telefonoNormalizado = usuarioService.normalizarTelefono(telefono);
            usuarioService.generarTokenRecuperacionPorTelefono(telefonoNormalizado);

            // Envía el OTP vía Twilio Verify
            boolean enviado = smsService.enviarSmsRecuperacion(telefonoNormalizado);

            if (enviado) {
                return ResponseEntity.ok(Map.of(
                    "mensaje",   "Se envió un código de verificación por SMS al " + telefonoNormalizado,
                    "telefono",  telefonoNormalizado,
                    "entregado", true
                ));
            } else {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                    "mensaje",   "El número está registrado pero no se pudo enviar el SMS. Intente de nuevo.",
                    "entregado", false
                ));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    /**
     * POST /api/usuarios/verificar-recuperacion
     * Verifica el código OTP ingresado por el usuario para recuperar contraseña.
     *
     * Body: { "telefono": "+573142733826", "codigo": "123456" }
     */
    @PostMapping("/verificar-recuperacion")
    public ResponseEntity<?> verificarRecuperacion(@RequestBody Map<String, String> body) {
        String telefono = body.get("telefono");
        String codigo   = body.get("codigo");

        if (telefono == null || codigo == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "Se requieren los campos 'telefono' y 'codigo'"));
        }

        boolean valido = smsService.verificarCodigo(telefono, codigo);

        if (valido) {
            // Recupera el token interno para que el frontend pueda llamar /restablecer
            try {
                String token = usuarioService.generarTokenRecuperacionPorTelefono(telefono);
                return ResponseEntity.ok(Map.of(
                    "verificado", true,
                    "token",      token,
                    "mensaje",    "Código verificado. Puede restablecer su contraseña."
                ));
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "verificado", false,
                "mensaje",    "Código incorrecto o expirado"
            ));
        }
    }

    /**
     * POST /api/usuarios/restablecer
     * Restablece la contraseña usando el token obtenido tras verificar el OTP.
     *
     * Body: { "token": "...", "nuevaPassword": "..." }
     */
    @PostMapping("/restablecer")
    public ResponseEntity<?> restablecerPassword(@RequestBody RestablecerRequest request) {
        try {
            usuarioService.restablecerPassword(request.getToken(), request.getNuevaPassword());
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RESTO DE ENDPOINTS (sin cambios)
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping
    public List<Usuario> obtenerUsuarios() {
        return usuarioService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Usuario u = usuarioService.obtenerPorId(id);
            return ResponseEntity.ok(u);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
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
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}/informe-academico")
    public ResponseEntity<com.studyhub.dto.ResumenAcademicoDTO> obtenerInformeAcademico(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioService.obtenerResumenAcademico(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

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
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<EstadisticasDTO> obtenerEstadisticas(@PathVariable Long id) {
        try {
            EstadisticasDTO estadisticas = usuarioService.obtenerEstadisticas(id);
            return ResponseEntity.ok(estadisticas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/preferencias")
    public ResponseEntity<?> obtenerPreferencias(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioService.obtenerPreferencias(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PutMapping("/{id}/preferencias")
    public ResponseEntity<?> guardarPreferencias(@PathVariable Long id, @RequestBody Map<String, Object> preferencias) {
        try {
            usuarioService.guardarPreferencias(id, preferencias);
            return ResponseEntity.ok(Map.of("mensaje", "Preferencias guardadas exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/{id}/subjects")
    public ResponseEntity<?> obtenerMateriasPorUsuario(@PathVariable Long id) {
        try {
            usuarioService.obtenerPorId(id);
            List<Asignatura> asignaturas = asignaturaService.findByUserId(id);
            List<SubjectSummaryDTO> subjects = asignaturas.stream().map(a -> {
                double progreso = notaService.calcularProgreso(a.getId());
                String status;
                if (progreso <= 0.0) {
                    status = "pendiente";
                } else if (progreso >= 100.0) {
                    status = "completada";
                } else {
                    status = "en_progreso";
                }
                String description = String.format("%s · %s · %d créditos",
                        a.getProfesor(), a.getPeriodo(), a.getCreditos());
                String redirectUrl = "/materias/" + a.getId() + "/calificaciones";
                return new SubjectSummaryDTO(a.getId(), a.getNombre(), description, status, redirectUrl, null);
            }).collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(subjects);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok(Map.of("mensaje", "Cuenta y todos sus datos asociados eliminados exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
        }
    }
}