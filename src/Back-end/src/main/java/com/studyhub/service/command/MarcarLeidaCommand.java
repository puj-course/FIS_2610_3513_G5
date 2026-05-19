package com.studyhub.service.command;

import com.studyhub.model.Notification;
import com.studyhub.repository.NotificationRepository;

/**
 * Command: marca una notificación como LEIDA.
 * Undo: restaura el estado anterior (NO_LEIDA u otro).
 */
public class MarcarLeidaCommand implements NotificacionCommand {

    private final NotificationRepository repo;
    private final Long notificacionId;

    /** Estado previo capturado en el momento de construir el command. */
    private String estadoPrevio;

    public MarcarLeidaCommand(NotificationRepository repo, Long notificacionId) {
        this.repo             = repo;
        this.notificacionId   = notificacionId;
    }

    @Override
    public void ejecutar() {
        Notification n = repo.findById(notificacionId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Notificación no encontrada: " + notificacionId));

        this.estadoPrevio = n.getStatus();   // snapshot antes de modificar
        n.setStatus("LEIDA");
        repo.save(n);
    }

    @Override
    public void deshacer() {
        if (estadoPrevio == null) return;    // ejecutar() nunca se llamó

        repo.findById(notificacionId).ifPresent(n -> {
            n.setStatus(estadoPrevio);
            repo.save(n);
        });
    }

    @Override
    public String getDescripcion() {
        return "Notificación marcada como leída";
    }
}