package com.studyhub.service.factory;

import com.studyhub.model.Notification;

public class NotificacionClaseProximaFactory extends NotificacionFactory {

    // datos[0] = String nombreMateria, datos[1] = String horaInicio
    @Override
    public Notification crear(Long userId, Object... datos) {
        String materia = (String) datos[0];
        String hora    = (String) datos[1];
        return construir(
            userId,
            "CALENDARIO",
            "Tu clase de " + materia + " comienza a las " + hora,
            "NORMAL",
            "#nav-horario"
        );
    }
}