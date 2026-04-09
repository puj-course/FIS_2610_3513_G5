package com.studyhub.controller;

import com.studyhub.dto.AuthResponse;
import com.studyhub.dto.LoginRequest;
import com.studyhub.dto.RegistroRequest;
import com.studyhub.service.AuthFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private AuthFacade authFacade;

    // Endpoint para crear un usuario (registro)
    @PostMapping
    public ResponseEntity<AuthResponse> registrar(@RequestBody RegistroRequest request) {
        AuthResponse response = authFacade.registrar(request);

        if (response.isExito()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authFacade.login(request);

        if (response.isExito()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // Endpoint para obtener todos los usuarios
    @GetMapping
    public List<Usuario> obtenerUsuarios() {
        return usuarioService.obtenerUsuarios();
    }

    // Endpoint para el login de usuario
    @PostMapping("/login")
    public Usuario login(@RequestBody Usuario usuario) {
        Usuario usuarioEncontrado = usuarioService.login(usuario);
        
        if (usuarioEncontrado != null) {
            return usuarioEncontrado; // Si las credenciales son correctas
        }
        
        throw new RuntimeException("Credenciales incorrectas");
    }
}