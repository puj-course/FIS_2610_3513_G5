package com.studyhub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "tareas")
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descripcion;
    private LocalDateTime recordatorio;
    private LocalDate fechaVencimiento;
    private boolean estado;

    public Tarea() {
    }

    public Tarea(String titulo, String descripcion, LocalDateTime recordatorio,
                 LocalDate fechaVencimiento, boolean estado) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.recordatorio = recordatorio;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getRecordatorio() { return recordatorio; }
    public void setRecordatorio(LocalDateTime recordatorio) { this.recordatorio = recordatorio; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Tarea{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", recordatorio=" + recordatorio +
                ", fechaVencimiento=" + fechaVencimiento +
                ", estado=" + estado +
                '}';
    }
}