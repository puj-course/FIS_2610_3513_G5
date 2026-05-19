package com.studyhub.service;

import com.studyhub.dto.AsignaturaResumenDTO;
import com.studyhub.dto.ResumenAcademicoDTO;
import com.studyhub.dto.TareaResumenDTO;
import com.studyhub.service.builder.ResumenAcademicoBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResumenAcademicoBuilderTest {

    @Test
    void testBuildCompleto() {
        AsignaturaResumenDTO asig = new AsignaturaResumenDTO("Cálculo", List.of(), 3.5, false);
        TareaResumenDTO tarea = new TareaResumenDTO("Taller", "Cálculo", LocalDate.of(2026, 6, 1), null);

        ResumenAcademicoDTO resumen = new ResumenAcademicoBuilder()
                .conNombreUsuario("Sarah Barrero")
                .conPromedioGlobal(3.5)
                .conAsignaturas(List.of(asig))
                .conTareasPendientes(List.of(tarea))
                .build();

        assertEquals("Sarah Barrero", resumen.getNombreUsuario());
        assertEquals(3.5, resumen.getPromedioGlobal());
        assertEquals(1, resumen.getAsignaturas().size());
        assertEquals("Cálculo", resumen.getAsignaturas().get(0).getNombre());
        assertEquals(1, resumen.getTareasPendientes().size());
        assertEquals("Taller", resumen.getTareasPendientes().get(0).getTitulo());
    }

    @Test
    void testPromedioSeRedondea() {
        ResumenAcademicoDTO resumen = new ResumenAcademicoBuilder()
                .conNombreUsuario("Test")
                .conPromedioGlobal(3.14159)
                .conAsignaturas(List.of())
                .conTareasPendientes(List.of())
                .build();

        assertEquals(3.14, resumen.getPromedioGlobal());
    }

    @Test
    void testListasVacias() {
        ResumenAcademicoDTO resumen = new ResumenAcademicoBuilder()
                .conNombreUsuario("Test")
                .conPromedioGlobal(0.0)
                .conAsignaturas(List.of())
                .conTareasPendientes(List.of())
                .build();

        assertNotNull(resumen.getAsignaturas());
        assertNotNull(resumen.getTareasPendientes());
        assertTrue(resumen.getAsignaturas().isEmpty());
        assertTrue(resumen.getTareasPendientes().isEmpty());
    }
}