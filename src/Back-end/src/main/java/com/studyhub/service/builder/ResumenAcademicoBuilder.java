package com.studyhub.service.builder;

import com.studyhub.dto.AsignaturaResumenDTO;
import com.studyhub.dto.ResumenAcademicoDTO;
import com.studyhub.dto.TareaResumenDTO;

import java.util.List;

public class ResumenAcademicoBuilder {

    private String nombreUsuario;
    private double promedioGlobal;
    private List<AsignaturaResumenDTO> asignaturas;
    private List<TareaResumenDTO> tareasPendientes;

    public ResumenAcademicoBuilder conNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
        return this;
    }

    public ResumenAcademicoBuilder conPromedioGlobal(double promedioGlobal) {
        this.promedioGlobal = Math.round(promedioGlobal * 100.0) / 100.0;
        return this;
    }

    public ResumenAcademicoBuilder conAsignaturas(List<AsignaturaResumenDTO> asignaturas) {
        this.asignaturas = asignaturas;
        return this;
    }

    public ResumenAcademicoBuilder conTareasPendientes(List<TareaResumenDTO> tareasPendientes) {
        this.tareasPendientes = tareasPendientes;
        return this;
    }

    public ResumenAcademicoDTO build() {
        return new ResumenAcademicoDTO(
            nombreUsuario,
            promedioGlobal,
            asignaturas,
            tareasPendientes
        );
    }
}