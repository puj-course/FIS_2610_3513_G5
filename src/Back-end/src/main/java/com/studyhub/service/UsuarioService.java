package com.studyhub.service;

import com.studyhub.model.Usuario;
import com.studyhub.repository.UsuarioRepository;
import com.studyhub.service.strategy.PasswordEncryptionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // CORREGIDO: PasswordEncryptionStrategy ahora existe (ver BCryptEncryptionStrategy)
    private final PasswordEncryptionStrategy encryptionStrategy;

    @Autowired
    public UsuarioService(PasswordEncryptionStrategy encryptionStrategy) {
        this.encryptionStrategy = encryptionStrategy;
    }

    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }
        usuario.setPassword(encryptionStrategy.encrypt(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public Usuario login(String correo, String password) {
        return usuarioRepository.findByCorreo(correo)
                .filter(u -> encryptionStrategy.matches(password, u.getPassword()))
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
    }

    // NUEVO: método requerido por UsuarioController
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }
}
