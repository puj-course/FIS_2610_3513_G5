package com.studyhub.service;

import com.studyhub.model.Usuario;
import com.studyhub.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Crear un nuevo usuario (registro)
    public Usuario crearUsuario(Usuario usuario) {

        // Validar correo único
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            usuario.setRol("USER"); // Valor por defecto
        }

        // Cifrar contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        return usuarioRepository.save(usuario);
    }

    // Obtener todos los usuarios
    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    // Lógica para login (verificación de credenciales)
    public Usuario login(Usuario usuario) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByCorreo(usuario.getCorreo());

        if (usuarioExistente.isPresent()
                && passwordEncoder.matches(usuario.getPassword(), usuarioExistente.get().getPassword())) {
            return usuarioExistente.get(); // Si las credenciales son correctas
        }

        return null; // Si no se encuentra el usuario o la contraseña es incorrecta
    }
}