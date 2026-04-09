package com.studyhub.service.strategy;

import com.studyhub.model.Tarea;

public interface EstadoStrategy {
    boolean aplica(Tarea tarea);
    String getEstado();
}
