package com.studyhub.service.strategy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Implementación de cifrado usando BCrypt.
 * Spring la registra como Bean para que UsuarioService pueda inyectarla.
 */
@Component
public class BCryptEncryptionStrategy implements PasswordEncryptionStrategy {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String encrypt(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
