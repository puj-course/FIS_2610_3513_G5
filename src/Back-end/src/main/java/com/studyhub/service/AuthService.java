package com.studyhub.service;

import com.studyhub.model.PasswordResetToken;
import com.studyhub.model.SesionInvalidada;
import com.studyhub.model.Usuario;
import com.studyhub.repository.PasswordResetTokenRepository;
import com.studyhub.repository.SesionInvalidadaRepository;
import com.studyhub.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private SesionInvalidadaRepository sesionInvalidadaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /** Registra el logout del usuario en la blacklist. */
    public SesionInvalidada invalidarSesion(Long usuarioId) {
        return sesionInvalidadaRepository.save(new SesionInvalidada(usuarioId));
    }

    /** Retorna true si la sesión con ese loginAt sigue vigente. */
    public boolean esSesionValida(Long usuarioId, LocalDateTime loginAt) {
        if (loginAt == null) return false;
        return !sesionInvalidadaRepository
                .existsByUsuarioIdAndFechaLogoutAfter(usuarioId, loginAt);
    }

    // ========== FUNCIONES DE RECUPERACIÓN (HU-26) ==========

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }

    public void solicitarRecuperacion(String correo) {
        Optional<Usuario> userOpt = usuarioRepository.findByCorreo(correo);
        if (userOpt.isPresent()) {
            Usuario usuario = userOpt.get();
            String rawToken = UUID.randomUUID().toString();
            String hashedToken = hashToken(rawToken);
            String url = "http://localhost:5500/src/Front-End/index.html?token=" + rawToken;

            PasswordResetToken resetToken = new PasswordResetToken(
                    hashedToken,
                    usuario,
                    LocalDateTime.now().plusMinutes(30)
            );
            tokenRepository.save(resetToken);

            // Simulación en consola (Sprint 8: Simulación de Email)
            System.out.println("==========================================================");
            System.out.println("SIMULACIÓN DE ENVÍO DE CORREO - STUDYHUB");
            System.out.println("Para: " + correo);
            System.out.println("Enlace de recuperación: " + url);
            System.out.println("==========================================================");
        }
    }

    public boolean restablecerPassword(String rawToken, String nuevaPassword) {
        String hashedToken = hashToken(rawToken);
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(hashedToken);

        if (tokenOpt.isEmpty()) return false;

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.isUsado() || resetToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            return false;
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        resetToken.setUsado(true);
        tokenRepository.save(resetToken);

        return true;
    }
}
