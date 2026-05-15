package com.studyhub.service;

import com.studyhub.model.Asignacion;
import com.studyhub.model.Turno;
import com.studyhub.model.Usuario;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ExportServiceTest {

    private final ExportService exportService = new ExportService();

    @Test
    void generarPdf_retornaStreamConContenido() {
        Usuario u = new Usuario(); u.setNombre("Test");
        Turno t = new Turno(); t.setNombre("Mañana");
        Asignacion a = new Asignacion(); a.setUsuario(u); a.setTurno(t); a.setProyecto("P1"); a.setDate(java.time.LocalDate.now());

        ByteArrayInputStream result = exportService.generarPdf(List.of(a));
        assertNotNull(result);
        assertTrue(result.available() > 0);
    }

    @Test
    void generarCsv_retornaStringConCabeceraYDatos() {
        Usuario u = new Usuario(); u.setNombre("Test");
        Turno t = new Turno(); t.setNombre("Mañana");
        Asignacion a = new Asignacion(); a.setUsuario(u); a.setTurno(t); a.setProyecto("P1"); a.setDate(java.time.LocalDate.now()); a.setHorasDiarias(5); a.setTieneConflicto(false);

        String result = exportService.generarCsv(List.of(a));
        assertTrue(result.contains("Fecha,Proyecto,Usuario,Turno,Horas,Conflicto"));
        assertTrue(result.contains("P1"));
    }
}
