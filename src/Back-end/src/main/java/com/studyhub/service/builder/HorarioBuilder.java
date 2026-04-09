package com.studyhub.service.builder;

import com.studyhub.dto.horarioDTO;

public class HorarioBuilder {
    private horarioDTO dto = new horarioDTO();

    public HorarioBuilder conAsignatura(String nombre) {
        // CORREGIDO: se llama setNombreAsignatura() (antes era setNombreNombreAsignatura())
        dto.setNombreAsignatura(nombre);
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

    public HorarioBuilder conAula(String aula) {
        dto.setAula(aula);
        return this;
    }

    public HorarioBuilder conProfesor(String profesor) {
        dto.setProfesor(profesor);
        return this;
    }

    public horarioDTO build() {
        return dto;
    }
}
