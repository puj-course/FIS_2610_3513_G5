package com.studyhub.service;

import com.studyhub.dto.horarioDTO;
import com.studyhub.model.Asignatura;
import com.studyhub.repository.HorarioRepository;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class HorarioServiceTest {

    private final HorarioRepository repo = new HorarioRepository();

    @Test
    void testDTOConSalonRetornaDatosCorrectos() {
        Asignatura a = new Asignatura();
        a.setNombre("Cálculo");
        a.setDiasClase("Lunes");
        a.setHoraInicio("08:00");
        a.setHoraFin("10:00");
        a.setProfesor("Dr. López");
        a.setSalonNombre("Salón 301");
        a.setSalonUbicacion("Edificio A");
        a.setSalonCapacidad(40);

        List<horarioDTO> result = repo.transformToDTO(List.of(a));

        assertEquals(1, result.size());
        assertEquals("Salón 301", result.get(0).getSalonNombre());
        assertEquals("Edificio A", result.get(0).getSalonUbicacion());
        assertEquals(40, result.get(0).getSalonCapacidad());
    }

    @Test
    void testDTOSinSalonRetornaNulos() {
        Asignatura a = new Asignatura();
        a.setNombre("Historia");
        a.setDiasClase("Martes");
        a.setHoraInicio("14:00");
        a.setHoraFin("16:00");
        // Sin datos de salón

        List<horarioDTO> result = repo.transformToDTO(List.of(a));

        assertEquals(1, result.size());
        assertNull(result.get(0).getSalonNombre());
        assertNull(result.get(0).getSalonUbicacion());
        assertNull(result.get(0).getSalonCapacidad());
    }
}
