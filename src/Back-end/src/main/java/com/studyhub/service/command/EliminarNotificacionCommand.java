package com.studyhub.service.command;

import com.studyhub.model.Notification;
import com.studyhub.repository.NotificationRepository;

/**
 * Command: elimina una notificación de la base de datos.
 * Undo: la restaura usando el snapshot guardado antes del borrado.
 *
 * Nota: al restaurar, JPA asigna un nuevo ID porque el registro
 * original fue eliminado físicamente. El frontend recibe el nuevo ID
 * en la respuesta del undo y actualiza su estado local.
 */
public class EliminarNotificacionCommand implements NotificacionCommand {

    private final NotificationRepository repo;
    private final Long notificacionId;

    /** Snapshot completo de la notificación antes de eliminarla. */
    private Notification snapshot;

    public EliminarNotificacionCommand(NotificationRepository repo, Long notificacionId) {
        this.repo           = repo;
        this.notificacionId = notificacionId;
    }

    @Override
    public void ejecutar() {
        Notification n = repo.findById(notificacionId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Notificación no encontrada: " + notificacionId));

        // Guardar snapshot completo antes de borrar
        this.snapshot = copiar(n);
        repo.deleteById(notificacionId);
    }

    @Override
    public void deshacer() {
        if (snapshot == null) return;   // ejecutar() nunca se llamó

        // Crear nueva entidad sin ID para que JPA la inserte como nueva fila
        Notification restaurada = new Notification();
        restaurada.setUserId(snapshot.getUserId());
        restaurada.setType(snapshot.getType());
        restaurada.setMessage(snapshot.getMessage());
        restaurada.setStatus(snapshot.getStatus());
        restaurada.setPriority(snapshot.getPriority());
        restaurada.setActionUrl(snapshot.getActionUrl());
        restaurada.setMetadata(snapshot.getMetadata());
        repo.save(restaurada);
    }

    /** Devuelve el snapshot para que el controller pueda responder con datos útiles. */
    public Notification getSnapshot() {
        return snapshot;
    }

    @Override
    public String getDescripcion() {
        return "Notificación eliminada";
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Notification copiar(Notification src) {
        Notification c = new Notification();
        c.setUserId(src.getUserId());
        c.setType(src.getType());
        c.setMessage(src.getMessage());
        c.setStatus(src.getStatus());
        c.setPriority(src.getPriority());
        c.setActionUrl(src.getActionUrl());
        c.setMetadata(src.getMetadata());
        return c;
    }
}