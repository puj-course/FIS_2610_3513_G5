package com.studyhub.service.strategy;

import com.studyhub.model.Tarea;

public class EstadoPendienteStrategy implements EstadoStrategy {

    @Override
    public boolean aplica(Tarea tarea) {
        return true;
    }

    @Override
    public String getEstado() {
        return "PENDIENTE";
    }
}
