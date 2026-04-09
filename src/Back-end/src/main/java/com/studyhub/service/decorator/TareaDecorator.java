package com.studyhub.service.decorator;

public abstract class TareaDecorator implements TareaBase {

    protected TareaBase tarea;

    public TareaDecorator(TareaBase tarea) {
        this.tarea = tarea;
    }

    @Override
    public String mostrar() {
        return tarea.mostrar();
    }
}
