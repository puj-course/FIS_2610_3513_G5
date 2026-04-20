package com.studyhub.controller;

import com.studyhub.dto.RecuperarRequest;
import com.studyhub.dto.RestablecerRequest;
import com.studyhub.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/recuperar")
    public ResponseEntity<Map<String, String>> recuperar(@RequestBody RecuperarRequest request) {
        authService.solicitarRecuperacion(request.getCorreo());
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Si el correo es válido, recibirá un enlace en breve.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/restablecer")
    public ResponseEntity<Map<String, String>> restablecer(@RequestBody RestablecerRequest request) {
        boolean exito = authService.restablecerPassword(request.getToken(), request.getNuevaPassword());
        Map<String, String> response = new HashMap<>();
        if (exito) {
            response.put("mensaje", "Contraseña restablecida exitosamente.");
            return ResponseEntity.ok(response);
        }
        response.put("error", "El token es inválido o ha expirado.");
        return ResponseEntity.badRequest().body(response);
    }
}
