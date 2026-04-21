package com.studyhub.controller;

import com.studyhub.dto.RecuperarRequest;
import com.studyhub.dto.RestablecerRequest;
import com.studyhub.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/recuperar")
    public ResponseEntity<Map<String, String>> recuperar(@RequestBody RecuperarRequest request) {
        // Ejecutamos la lógica de forma asíncrona o sincrónica (sincrónica en este caso simple)
        // El issue requiere retornar SIEMPRE HTTP 200 con mensaje genérico
        authService.solicitarRecuperacion(request.getCorreo());
        
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Si el correo existe en nuestro sistema, enviaremos un mensaje con las instrucciones para recuperar tu contraseña.");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/restablecer")
    public ResponseEntity<Map<String, String>> restablecer(@RequestBody RestablecerRequest request) {
        boolean exito = authService.restablecerPassword(request.getToken(), request.getNuevaPassword());
        Map<String, String> response = new HashMap<>();
        
        if (exito) {
            response.put("mensaje", "Contraseña restablecida exitosamente.");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "El token es inválido, ya fue usado o ha expirado.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
