package com.studyhub.service.strategy;

/**
 * Estrategia de cifrado de contraseñas.
 * Permite intercambiar el algoritmo de cifrado sin modificar UsuarioService.
 */
public interface PasswordEncryptionStrategy {
    String encrypt(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
