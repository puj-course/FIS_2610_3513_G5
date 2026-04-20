package com.studyhub.service;

import com.studyhub.model.SesionInvalidada;
import com.studyhub.repository.SesionInvalidadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private SesionInvalidadaRepository sesionInvalidadaRepository;

    /** Registra el logout del usuario en la blacklist. */
    public SesionInvalidada invalidarSesion(Long usuarioId) {
        return sesionInvalidadaRepository.save(new SesionInvalidada(usuarioId));
    }

    /**
     * Retorna true si la sesión con ese loginAt sigue vigente.
     * Es inválida si existe un logout registrado DESPUÉS de ese loginAt.
     */
    public boolean esSesionValida(Long usuarioId, LocalDateTime loginAt) {
        if (loginAt == null) return false;
        return !sesionInvalidadaRepository
                .existsByUsuarioIdAndFechaLogoutAfter(usuarioId, loginAt);
    }
}
