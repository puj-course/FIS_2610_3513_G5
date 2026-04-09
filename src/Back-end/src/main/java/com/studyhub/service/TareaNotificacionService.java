package com.studyhub.service;

import com.studyhub.model.Tarea;
import com.studyhub.service.decorator.TareaBase;
import com.studyhub.service.decorator.TareaProximaDecorator;
import com.studyhub.service.decorator.TareaSimple;
import com.studyhub.service.decorator.TareaVencidaDecorator;
import org.springframework.stereotype.Service;

@Service
public class TareaNotificacionService {

    private final EstadoTareaService estadoTareaService;

    public TareaNotificacionService(EstadoTareaService estadoTareaService) {
        this.estadoTareaService = estadoTareaService;
    }

    public String procesarTarea(Tarea tarea) {
        String estado = estadoTareaService.obtenerEstado(tarea);

        TareaBase base = new TareaSimple(tarea);

        switch (estado) {
            case "VENCIDA":
                base = new TareaVencidaDecorator(base);
                break;
            case "PROXIMA":
                base = new TareaProximaDecorator(base);
                break;
            default:
                break;
        }

        return base.mostrar();
    }
}
