package com.studyhub.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class TareaResumenDTO {
    private String titulo;
    private String asignaturaNombre;
    private LocalDate fechaEntrega;
    private LocalTime horaEntrega; // HU-calendario: hora de entrega para vista de tareas del día

    public TareaResumenDTO() {}

    public TareaResumenDTO(String titulo, String asignaturaNombre, LocalDate fechaEntrega, LocalTime horaEntrega) {
        this.titulo = titulo;
        this.asignaturaNombre = asignaturaNombre;
        this.fechaEntrega = fechaEntrega;
        this.horaEntrega = horaEntrega;
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAsignaturaNombre() { return asignaturaNombre; }
    public void setAsignaturaNombre(String asignaturaNombre) { this.asignaturaNombre = asignaturaNombre; }

    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public LocalTime getHoraEntrega() { return horaEntrega; }
    public void setHoraEntrega(LocalTime horaEntrega) { this.horaEntrega = horaEntrega; }
}