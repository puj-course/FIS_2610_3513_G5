package com.studyhub.dto;

public class horarioDTO {

    private String nombreAsignatura;
    private String dia;
    private String horaInicio;
    private String horaFin;
    private String aula;
    private String profesor;

    public horarioDTO() {}

    // CORREGIDO: getter renombrado de getPorfesor() a getProfesor()
    public String getProfesor() {
        return profesor;
    }

    public void setProfesor(String profesor) {
        this.profesor = profesor;
    }

    public String getAula() {
        return aula;
    }

    public void setAula(String aula) {
        this.aula = aula;
    }

    public String getNombreAsignatura() {
        return nombreAsignatura;
    }

    // CORREGIDO: setter renombrado de setNombreNombreAsignatura() a setNombreAsignatura()
    public void setNombreAsignatura(String nombreAsignatura) {
        this.nombreAsignatura = nombreAsignatura;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoras(String horaInicio, String horaFin) {
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    @Override
    public String toString() {
        return "HorarioDTO{" +
                "asignatura='" + nombreAsignatura + '\'' +
                ", dia='" + dia + '\'' +
                ", horaInicio='" + horaInicio + '\'' +
                ", horaFin='" + horaFin + '\'' +
                ", profesor='" + profesor + '\'' +
                ", aula='" + aula + '\'' +
                '}';
    }
}
