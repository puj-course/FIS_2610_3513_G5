package com.studyhub.dto;

import com.studyhub.model.Nota;
import java.util.List;

public class AsignaturaResumenDTO {
    private String nombre;
    private List<Nota> notas;
    private double promedio;

    public AsignaturaResumenDTO() {}

    public AsignaturaResumenDTO(String nombre, List<Nota> notas, double promedio) {
        this.nombre = nombre;
        this.notas = notas;
        this.promedio = promedio;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<Nota> getNotas() { return notas; }
    public void setNotas(List<Nota> notas) { this.notas = notas; }

    public double getPromedio() { return promedio; }
    public void setPromedio(double promedio) { this.promedio = promedio; }
}
