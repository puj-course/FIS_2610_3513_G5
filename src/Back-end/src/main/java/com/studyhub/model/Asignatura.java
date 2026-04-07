package com.studyhub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalTime;

@Entity
@Table(name = "asignaturas")
public class Asignatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String codigo;
    private String profesor;
    private String dias;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private int creditos;
    private String periodo;

    public Asignatura() {
    }

    public Asignatura(String nombre, String codigo, String profesor,
                      String dias, LocalTime horaInicio, LocalTime horaFin, int creditos, String periodo) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.profesor = profesor;
        this.dias = dias;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.creditos = creditos;
        this.periodo = periodo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getProfesor() { return profesor; }
    public void setProfesor(String profesor) { this.profesor = profesor; }

    public String getDias() { return dias; }
    public void setDias(String dias) { this.dias = dias; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public int getCreditos() { return creditos; }
    public void setCreditos(int creditos) { this.creditos = creditos; }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    @Override
    public String toString() {
        return "Asignatura{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", codigo='" + codigo + '\'' +
                ", profesor='" + profesor + '\'' +
                ", dias='" + dias + '\'' +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                ", creditos=" + creditos +
                ", periodo='" + periodo + '\'' +
                '}';
    }
}
