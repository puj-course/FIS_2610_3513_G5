package com.studyhub.service.strategy;

import com.studyhub.model.Tarea;

import java.time.LocalDate;

public class EstadoVencidaStrategy implements EstadoStrategy {

    @Override
    public boolean aplica(Tarea tarea) {
        return tarea.getFechaEntrega().isBefore(LocalDate.now());
    }

    @Override
    public String getEstado() {
        return "VENCIDA";
    }
}
