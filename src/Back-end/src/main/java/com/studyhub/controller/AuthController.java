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

    // --- Endpoints Existentes en Main ---

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) Map<String, Object> body) {
        try {
            Long usuarioId = null;
            if (authHeader != null && !authHeader.isBlank()) {
                usuarioId = Long.valueOf(authHeader.trim());
            } else if (body != null && body.get("usuarioId") != null) {
                usuarioId = Long.valueOf(body.get("usuarioId").toString());
            }
            if (usuarioId == null) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "Se requiere identificador de usuario"));
            }
            authService.invalidarSesion(usuarioId);
            return ResponseEntity.ok(Map.of("exito", true, "mensaje", "Sesión cerrada"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("mensaje", "Error al cerrar sesión"));
        }
    }

    @GetMapping("/validar-sesion")
    public ResponseEntity<?> validarSesion(
            @RequestParam Long usuarioId,
            @RequestParam String loginAt) {
        try {
            LocalDateTime loginAtDt = LocalDateTime.parse(loginAt);
            boolean valida = authService.esSesionValida(usuarioId, loginAtDt);
            return ResponseEntity.ok(Map.of("valida", valida, "usuarioId", usuarioId));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Formato de fecha inválido"));
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
