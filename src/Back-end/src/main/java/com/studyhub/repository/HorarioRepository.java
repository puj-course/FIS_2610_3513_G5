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

            horarioDTO dto = new HorarioBuilder()
                    .conAsignatura(asignatura.getNombre())
                    .enDia("Lunes") // luego mejoramos con horario real
                    .conFranja("08:00", "10:00") // temporal
                    .build();

            horario.add(dto);
        }

        return horario;
    }
}
