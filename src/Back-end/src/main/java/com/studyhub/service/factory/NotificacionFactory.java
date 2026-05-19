package com.studyhub.service.factory;

import com.studyhub.model.Notification;

public abstract class NotificacionFactory {

    /**
     * Factory Method: cada subclase implementa cómo construir su notificación.
     * @param userId  destinatario
     * @param datos   contexto variable según el tipo (nombre materia, fecha, etc.)
     */
    public abstract Notification crear(Long userId, Object... datos);

    // Método auxiliar compartido para construir el objeto base
    protected Notification construir(Long userId, String type, String message,
                                      String priority, String actionUrl) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setMessage(message);
        n.setPriority(priority);
        n.setActionUrl(actionUrl);
        return n;
    }
}