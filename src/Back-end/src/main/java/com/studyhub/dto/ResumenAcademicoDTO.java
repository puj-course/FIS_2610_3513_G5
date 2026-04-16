package com.studyhub.dto;

import java.util.List;

public class ResumenAcademicoDTO {
    private String nombreUsuario;
    private double promedioGlobal;
    private List<AsignaturaResumenDTO> asignaturas;
    private List<TareaResumenDTO> tareasPendientes;

    public ResumenAcademicoDTO() {}

    public ResumenAcademicoDTO(String nombreUsuario, double promedioGlobal, 
                               List<AsignaturaResumenDTO> asignaturas, 
                               List<TareaResumenDTO> tareasPendientes) {
        this.nombreUsuario = nombreUsuario;
        this.promedioGlobal = promedioGlobal;
        this.asignaturas = asignaturas;
        this.tareasPendientes = tareasPendientes;
    }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public double getPromedioGlobal() { return promedioGlobal; }
    public void setPromedioGlobal(double promedioGlobal) { this.promedioGlobal = promedioGlobal; }

    public List<AsignaturaResumenDTO> getAsignaturas() { return asignaturas; }
    public void setAsignaturas(List<AsignaturaResumenDTO> asignaturas) { this.asignaturas = asignaturas; }

    public List<TareaResumenDTO> getTareasPendientes() { return tareasPendientes; }
    public void setTareasPendientes(List<TareaResumenDTO> tareasPendientes) { this.tareasPendientes = tareasPendientes; }
}
