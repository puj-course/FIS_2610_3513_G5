package com.studyhub.service.decorator;

public class TareaProximaDecorator extends TareaDecorator {

    public TareaProximaDecorator(TareaBase tarea) {
        super(tarea);
    }

    @Override
    public String mostrar() {
        return super.mostrar() + " 🟡 (PRÓXIMA)";
    }
}
