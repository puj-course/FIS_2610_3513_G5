package com.studyhub.service.strategy;

import com.studyhub.model.Tarea;

import java.time.LocalDate;

public class EstadoProximaStrategy implements EstadoStrategy {

    @Override
    public boolean aplica(Tarea tarea) {
        return tarea.getFechaEntrega().isEqual(LocalDate.now().plusDays(1));
    }

    @Override
    public String getEstado() {
        return "PROXIMA";
    }
}