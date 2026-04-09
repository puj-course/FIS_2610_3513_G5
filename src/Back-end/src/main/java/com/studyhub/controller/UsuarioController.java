package com.studyhub.controller;

import com.studyhub.model.Usuario;
import com.studyhub.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Endpoint para crear un usuario (registro)
    @PostMapping
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        return usuarioService.crearUsuario(usuario);
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