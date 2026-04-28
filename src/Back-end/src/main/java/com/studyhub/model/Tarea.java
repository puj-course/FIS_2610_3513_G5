package com.studyhub.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "tareas")
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @ManyToOne
    @JoinColumn(name = "asignatura_id", nullable = false)
    private Asignatura asignatura;

    @Column(nullable = false)
    private LocalDate fechaEntrega;

    @Column(nullable = false)
    private LocalTime horaEntrega;

    @Column(nullable = true)
    private String descripcion;

    @Column(nullable = false)
    private boolean estado;

    /** Prioridad de la tarea: alta, media, baja */
    @Column(nullable = false)
    private String prioridad = "media";

    public Tarea() {
    }

    public Tarea(String titulo, Asignatura asignatura, LocalDate fechaEntrega,
                 LocalTime horaEntrega, String descripcion, boolean estado, String prioridad) {
        this.titulo = titulo;
        this.asignatura = asignatura;
        this.fechaEntrega = fechaEntrega;
        this.horaEntrega = horaEntrega;
        this.descripcion = descripcion;
        this.estado = estado;
        this.prioridad = prioridad != null ? prioridad : "media";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Asignatura getAsignatura() { return asignatura; }
    public void setAsignatura(Asignatura asignatura) { this.asignatura = asignatura; }

    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public LocalTime getHoraEntrega() { return horaEntrega; }
    public void setHoraEntrega(LocalTime horaEntrega) { this.horaEntrega = horaEntrega; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad != null ? prioridad : "media"; }

    @Override
    public String toString() {
        return "Tarea{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", asignatura=" + (asignatura != null ? asignatura.getNombre() : "null") +
                ", fechaEntrega=" + fechaEntrega +
                ", horaEntrega=" + horaEntrega +
                ", descripcion='" + descripcion + '\'' +
                ", prioridad='" + prioridad + '\'' +
                ", estado=" + estado +
                '}';
    }
}