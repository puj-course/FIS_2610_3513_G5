package com.studyhub.dto;

public class UsuarioResumenDTO {

    private Long id;
    private String nombre;
    private int totalAsignaturas;
    private double promedioGlobal;

    public UsuarioResumenDTO() {
    }

    public UsuarioResumenDTO(Long id, String nombre, int totalAsignaturas, double promedioGlobal) {
        this.id = id;
        this.nombre = nombre;
        this.totalAsignaturas = totalAsignaturas;
        this.promedioGlobal = promedioGlobal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTotalAsignaturas() {
        return totalAsignaturas;
    }

    public void setTotalAsignaturas(int totalAsignaturas) {
        this.totalAsignaturas = totalAsignaturas;
    }

    public double getPromedioGlobal() {
        return promedioGlobal;
    }

    public void setPromedioGlobal(double promedioGlobal) {
        this.promedioGlobal = promedioGlobal;
    }
}
