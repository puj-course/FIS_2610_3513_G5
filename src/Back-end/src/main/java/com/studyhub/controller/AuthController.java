package com.studyhub.controller;

import com.studyhub.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * POST /auth/logout
     * Header esperado: Authorization: <usuarioId>
     * (En este proyecto no hay JWT; se usa el id directamente como token de sesión)
     * Body alternativo: { "usuarioId": 5 }
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) Map<String, Object> body) {
        try {
            // Leer usuarioId del header o del body (compatibilidad con ambas formas)
            Long usuarioId = null;
            if (authHeader != null && !authHeader.isBlank()) {
                usuarioId = Long.valueOf(authHeader.trim());
            } else if (body != null && body.get("usuarioId") != null) {
                usuarioId = Long.valueOf(body.get("usuarioId").toString());
            }

            if (usuarioId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("mensaje", "Se requiere el header Authorization o el campo usuarioId en el body"));
            }

            authService.invalidarSesion(usuarioId);

            return ResponseEntity.ok(Map.of(
                "exito",   true,
                "mensaje", "Sesión cerrada exitosamente"
            ));

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "El identificador de usuario debe ser numérico"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("mensaje", "Error al cerrar la sesión: " + e.getMessage()));
        }
    }

    /**
     * GET /auth/validar-sesion?usuarioId=5&loginAt=2026-04-19T10:30:00
     * El middleware del front llama esto en cada cambio de ruta.
     */
    @GetMapping("/validar-sesion")
    public ResponseEntity<?> validarSesion(
            @RequestParam Long usuarioId,
            @RequestParam String loginAt) {
        try {
            LocalDateTime loginAtDt = LocalDateTime.parse(loginAt);
            boolean valida = authService.esSesionValida(usuarioId, loginAtDt);
            return ResponseEntity.ok(Map.of("valida", valida, "usuarioId", usuarioId));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "Formato inválido. Use: yyyy-MM-ddTHH:mm:ss"));
        }
    }

    // --- NUEVOS Endpoints de Recuperación (HU-26) ---

    @PostMapping("/recuperar")
    public ResponseEntity<?> solicitarRecuperacion(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        if (correo == null || correo.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El correo es obligatorio"));
        }
        authService.solicitarRecuperacion(correo);
        return ResponseEntity.ok(Map.of("mensaje", "Si el correo existe, se enviará un enlace de recuperación."));
    }

    @PostMapping("/restablecer")
    public ResponseEntity<?> restablecerPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String nuevaPassword = body.get("password");
        if (token == null || nuevaPassword == null || nuevaPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Token inválido o contraseña demasiado corta."));
        }
        boolean exito = authService.restablecerPassword(token, nuevaPassword);
        if (exito) {
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada con éxito."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El enlace ha expirado o es inválido."));
        }
    }
}
