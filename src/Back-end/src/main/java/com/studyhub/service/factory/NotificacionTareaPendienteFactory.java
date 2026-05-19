package com.studyhub.service.factory;

import com.studyhub.model.Notification;

public class NotificacionTareaPendienteFactory extends NotificacionFactory {

    // datos[0] = String nombreTarea, datos[1] = String fechaLimite
    @Override
    public Notification crear(Long userId, Object... datos) {
        String tarea = (String) datos[0];
        String fecha = (String) datos[1];
        return construir(
            userId,
            "TAREA",
            "La tarea \"" + tarea + "\" vence el " + fecha,
            "CRITICA",
            "#nav-tasks"
        );
    }
}