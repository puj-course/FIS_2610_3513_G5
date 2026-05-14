package com.studyhub.service;

import com.studyhub.model.Asignacion;
import com.studyhub.model.Cronograma;
import com.studyhub.model.Turno;
import com.studyhub.repository.AsignacionRepository;
import com.studyhub.repository.CronogramaRepository;
import com.studyhub.repository.TurnoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CronogramaService {

    private final CronogramaRepository cronogramaRepo;
    private final AsignacionRepository asignacionRepo;
    private final TurnoRepository turnoRepo;

    public CronogramaService(CronogramaRepository cronogramaRepo, 
                             AsignacionRepository asignacionRepo,
                             TurnoRepository turnoRepo) {
        this.cronogramaRepo = cronogramaRepo;
        this.asignacionRepo = asignacionRepo;
        this.turnoRepo = turnoRepo;
    }

    public List<Asignacion> obtenerAsignacionesPorSemana(LocalDate fechaInicio) {
        Optional<Cronograma> crono = cronogramaRepo.findByFechaInicioSemana(fechaInicio);
        return crono.map(c -> asignacionRepo.findByCronogramaId(c.getId()))
                    .orElse(List.of());
    }

    public Asignacion guardarAsignacion(Asignacion asignacion) {
        // Lógica de detección de conflictos (Sub-issue #477)
        detectarConflictos(asignacion);
        return asignacionRepo.save(asignacion);
    }

    private void detectarConflictos(Asignacion nueva) {
        List<Asignacion> existentes = asignacionRepo.findByUsuarioId(nueva.getUsuario().getId());
        
        boolean conflicto = existentes.stream()
            .filter(a -> !a.getId().equals(nueva.getId()))
            .filter(a -> a.getFecha().equals(nueva.getFecha()))
            .anyMatch(a -> solapan(a.getTurno(), nueva.getTurno()));

        // También podemos validar exceso de horas (ej: > 10 horas al día)
        int horasTotales = existentes.stream()
            .filter(a -> a.getFecha().equals(nueva.getFecha()))
            .mapToInt(Asignacion::getHorasDiarias)
            .sum() + nueva.getHorasDiarias();

        if (conflicto || horasTotales > 10) {
            nueva.setTieneConflicto(true);
        } else {
            nueva.setTieneConflicto(false);
        }
    }

    private boolean solapan(Turno t1, Turno t2) {
        return t1.getHoraInicio().isBefore(t2.getHoraFin()) && 
               t2.getHoraInicio().isBefore(t1.getHoraFin());
    }
}
