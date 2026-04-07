package com.studyhub.service;

import com.studyhub.model.Tarea;
import com.studyhub.service.strategy.EstadoPendienteStrategy;
import com.studyhub.service.strategy.EstadoProximaStrategy;
import com.studyhub.service.strategy.EstadoStrategy;
import com.studyhub.service.strategy.EstadoVencidaStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoTareaService {

    private final List<EstadoStrategy> strategies;

    public EstadoTareaService() {
        this.strategies = List.of(
                new EstadoVencidaStrategy(),
                new EstadoProximaStrategy(),
                new EstadoPendienteStrategy()
        );
    }

    public String obtenerEstado(Tarea tarea) {
        for (EstadoStrategy strategy : strategies) {
            if (strategy.aplica(tarea)) {
                return strategy.getEstado();
            }
        }
        return "DESCONOCIDO";
    }
}
