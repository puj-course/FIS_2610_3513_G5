package com.studyhub.service.decorator;

public class TareaVencidaDecorator extends TareaDecorator {

    public TareaVencidaDecorator(TareaBase tarea) {
        super(tarea);
    }

    @Override
    public String mostrar() {
        return super.mostrar() + " 🔴 (VENCIDA)";
    }
}
