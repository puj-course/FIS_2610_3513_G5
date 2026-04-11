package com.studyhub.service.decorator;

import com.studyhub.model.Tarea;

public class TareaSimple implements TareaBase {

    private final Tarea tarea;

    public TareaSimple(Tarea tarea) {
        this.tarea = tarea;
    }

    @Override
    public String mostrar() {
        return tarea.getTitulo();
    }
}
