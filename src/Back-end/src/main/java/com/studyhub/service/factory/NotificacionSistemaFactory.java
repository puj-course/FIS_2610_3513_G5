package com.studyhub.service.factory;

import com.studyhub.model.Notification;

public class NotificacionSistemaFactory extends NotificacionFactory {

    // datos[0] = String mensaje
    @Override
    public Notification crear(Long userId, Object... datos) {
        return construir(userId, "SISTEMA", (String) datos[0], "NORMAL", null);
    }
}