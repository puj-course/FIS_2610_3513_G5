package com.studyhub.service.factory;

import com.studyhub.model.Notification;

public class NotificacionMateriaEnRiesgoFactory extends NotificacionFactory {

    // datos[0] = String nombreMateria, datos[1] = Double promedioActual
    @Override
    public Notification crear(Long userId, Object... datos) {
        String materia  = (String) datos[0];
        Double promedio = (Double) datos[1];
        return construir(
            userId,
            "CALIFICACION",
            "Tu promedio en " + materia + " es " + promedio + ". ¡Estás en riesgo!",
            "CRITICA",
            "#nav-resenas"
        );
    }
}