package com.studyhub.repository;

import com.studyhub.dto.horarioDTO;
import com.studyhub.model.Asignatura;
import com.studyhub.service.builder.HorarioBuilder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class HorarioRepository {

    public List<horarioDTO> transformToDTO(List<Asignatura> asignaturas) {

        List<horarioDTO> horario = new ArrayList<>();

        for (Asignatura asignatura : asignaturas) {

            // Si no tiene días/horas configurados, se omite del horario
            if (asignatura.getDiasClase() == null || asignatura.getDiasClase().isBlank()) {
                continue;
            }

            String[] dias = asignatura.getDiasClase().split(",");
            for (String dia : dias) {
                horarioDTO dto = new HorarioBuilder()
                        .conAsignatura(asignatura.getNombre())
                        .enDia(dia.trim())
                        .conFranja(
                            asignatura.getHoraInicio() != null ? asignatura.getHoraInicio() : "00:00",
                            asignatura.getHoraFin()    != null ? asignatura.getHoraFin()    : "00:00"
                        )
                        .conProfesor(asignatura.getProfesor())
                        .conSalon(asignatura.getSalonNombre(), asignatura.getSalonUbicacion(), asignatura.getSalonCapacidad())
                        .build();

                horario.add(dto);
            }
        }

        return horario;
    }
}
