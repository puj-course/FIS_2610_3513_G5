package com.studyhub.service.strategy;

public interface PasswordEncryptionStrategy {

    String encrypt(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
