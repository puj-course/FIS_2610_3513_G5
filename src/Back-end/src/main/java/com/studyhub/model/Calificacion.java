package com.studyhub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "calificaciones")
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Double calificacion;
    private Double porcentaje;

    @ManyToOne
    @JoinColumn(name = "asignatura_id")
    private Asignatura asignatura;

    public Calificacion() {
    }

    public Calificacion(String nombre, Double calificacion, Double porcentaje, Asignatura asignatura) {
        this.nombre = nombre;
        this.calificacion = calificacion;
        this.porcentaje = porcentaje;
        this.asignatura = asignatura;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getCalificacion() { return calificacion; }
    public void setCalificacion(Double calificacion) { this.calificacion = calificacion; }

    public Double getPorcentaje() { return porcentaje; }
    public void setPorcentaje(Double porcentaje) { this.porcentaje = porcentaje; }

    public Asignatura getAsignatura() { return asignatura; }
    public void setAsignatura(Asignatura asignatura) { this.asignatura = asignatura; }

    @Override
    public String toString() {
        return "Calificacion{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", calificacion=" + calificacion +
                ", porcentaje=" + porcentaje +
                ", asignatura=" + (asignatura != null ? asignatura.getNombre() : "null") +
                '}';
    }
}
