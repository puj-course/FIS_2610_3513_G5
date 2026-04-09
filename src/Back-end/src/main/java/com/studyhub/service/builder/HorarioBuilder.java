package com.studyhub.service.builder;
import com.studyhub.dto.horarioDTO;

public class HorarioBuilder {
    private horarioDTO dto = new horarioDTO();

    public HorarioBuilder conAsignatura(String nombre) {
        dto.setNombreNombreAsignatura(nombre);
        return this;
    }

    public HorarioBuilder enDia(String dia) {
        dto.setDia(dia);
        return this;
    }

    public HorarioBuilder conFranja(String inicio, String fin) {
        dto.setHoras(inicio, fin);
        return this;
    }

    public horarioDTO build() {
        return dto;
    }
}
