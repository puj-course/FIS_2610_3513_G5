package com.studyhub.dto;

import java.time.LocalDate;

public class TareaResumenDTO {
    private String titulo;
    private String asignaturaNombre;
    private LocalDate fechaEntrega;

    public TareaResumenDTO() {}

    public TareaResumenDTO(String titulo, String asignaturaNombre, LocalDate fechaEntrega) {
        this.titulo = titulo;
        this.asignaturaNombre = asignaturaNombre;
        this.fechaEntrega = fechaEntrega;
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAsignaturaNombre() { return asignaturaNombre; }
    public void setAsignaturaNombre(String asignaturaNombre) { this.asignaturaNombre = asignaturaNombre; }

    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }
}
