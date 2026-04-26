package com.studyhub.dto;

import java.util.Map;

public class EstadisticasDTO {
    private double promedioGlobal;
    private int totalMaterias;
    private int materiasEnRiesgo;
    private int totalCreditos;
    private Map<String, Double> promediosPorMateria;

    public EstadisticasDTO(double promedioGlobal, int totalMaterias, int materiasEnRiesgo, int totalCreditos, Map<String, Double> promediosPorMateria) {
        this.promedioGlobal = promedioGlobal;
        this.totalMaterias = totalMaterias;
        this.materiasEnRiesgo = materiasEnRiesgo;
        this.totalCreditos = totalCreditos;
        this.promediosPorMateria = promediosPorMateria;
    }

    // Getters
    public double getPromedioGlobal() { return promedioGlobal; }
    public int getTotalMaterias() { return totalMaterias; }
    public int getMateriasEnRiesgo() { return materiasEnRiesgo; }
    public int getTotalCreditos() { return totalCreditos; }
    public Map<String, Double> getPromediosPorMateria() { return promediosPorMateria; }

    // Setters
    public void setPromedioGlobal(double promedioGlobal) { this.promedioGlobal = promedioGlobal; }
    public void setTotalMaterias(int totalMaterias) { this.totalMaterias = totalMaterias; }
    public void setMateriasEnRiesgo(int materiasEnRiesgo) { this.materiasEnRiesgo = materiasEnRiesgo; }
    public void setTotalCreditos(int totalCreditos) { this.totalCreditos = totalCreditos; }
    public void setPromediosPorMateria(Map<String, Double> promediosPorMateria) { this.promediosPorMateria = promediosPorMateria; }
}
