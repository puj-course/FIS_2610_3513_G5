package com.studyhub.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cronogramas")
public class Cronograma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaInicioSemana; // Lunes de la semana
    private String estado; // Ej: "ABIERTO", "CERRADO"

    public Cronograma() {}

    public Cronograma(LocalDate fechaInicioSemana, String estado) {
        this.fechaInicioSemana = fechaInicioSemana;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFechaInicioSemana() { return fechaInicioSemana; }
    public void setFechaInicioSemana(LocalDate fechaInicioSemana) { this.fechaInicioSemana = fechaInicioSemana; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
