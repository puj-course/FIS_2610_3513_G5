package com.studyhub.service;

import com.studyhub.model.Asignacion;
import com.studyhub.model.Cronograma;
import com.studyhub.model.Turno;
import com.studyhub.model.Usuario;
import com.studyhub.repository.AsignacionRepository;
import com.studyhub.repository.CronogramaRepository;
import com.studyhub.repository.TurnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(TestResultLogger.class)
class CronogramaServiceTest {

    @Mock
    private CronogramaRepository cronogramaRepo;

    @Mock
    private AsignacionRepository asignacionRepo;

    @Mock
    private TurnoRepository turnoRepo;

    @InjectMocks
    private CronogramaService cronogramaService;

    private Usuario usuario;
    private Turno turnoManana;
    private Turno turnoTarde;
    private Cronograma cronograma;
    private Asignacion asignacion;
    private LocalDate fechaSemana;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Carlos");

        turnoManana = new Turno("Mañana", LocalTime.of(8, 0), LocalTime.of(12, 0));
        turnoManana.setId(1L);

        turnoTarde = new Turno("Tarde", LocalTime.of(13, 0), LocalTime.of(17, 0));
        turnoTarde.setId(2L);

        fechaSemana = LocalDate.of(2026, 5, 11); // Lunes
        cronograma = new Cronograma(fechaSemana, "ABIERTO");
        cronograma.setId(1L);

        asignacion = new Asignacion();
        asignacion.setId(1L);
        asignacion.setUsuario(usuario);
        asignacion.setTurno(turnoManana);
        asignacion.setCronograma(cronograma);
        asignacion.setProyecto("Proyecto A");
        asignacion.setDate(fechaSemana);
        asignacion.setHorasDiarias(4);
    }

    @Test
    void obtenerAsignacionesPorSemana_retornaLista_cuandoCronogramaExiste() {
        when(cronogramaRepo.findByFechaInicioSemana(fechaSemana)).thenReturn(Optional.of(cronograma));
        when(asignacionRepo.findByCronogramaId(1L)).thenReturn(List.of(asignacion));

        List<Asignacion> resultado = cronogramaService.obtenerAsignacionesPorSemana(fechaSemana);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Proyecto A", resultado.get(0).getProyecto());
        verify(cronogramaRepo, times(1)).findByFechaInicioSemana(fechaSemana);
    }

    @Test
    void obtenerAsignacionesPorSemana_retornaVacio_cuandoCronogramaNoExiste() {
        when(cronogramaRepo.findByFechaInicioSemana(fechaSemana)).thenReturn(Optional.empty());

        List<Asignacion> resultado = cronogramaService.obtenerAsignacionesPorSemana(fechaSemana);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(asignacionRepo, never()).findByCronogramaId(any());
    }

    @Test
    void guardarAsignacion_guardaSinConflicto_cuandoNoHaySolapamientoNiExcesoDeHoras() {
        when(asignacionRepo.findByUsuarioId(1L)).thenReturn(Collections.emptyList());
        when(asignacionRepo.save(asignacion)).thenReturn(asignacion);

        Asignacion guardada = cronogramaService.guardarAsignacion(asignacion);

        assertNotNull(guardada);
        assertFalse(guardada.getTieneConflicto(), "No debe marcar conflicto si está libre");
        verify(asignacionRepo, times(1)).save(asignacion);
    }

    @Test
    void guardarAsignacion_detectaConflicto_cuandoTurnosSolapan() {
        // Arrange: creamos una asignación existente en el mismo horario
        Asignacion existente = new Asignacion();
        existente.setId(2L);
        existente.setUsuario(usuario);
        // Mismo turno o solapado
        Turno turnoSolapado = new Turno("Mañana Corta", LocalTime.of(9, 0), LocalTime.of(11, 0));
        existente.setTurno(turnoSolapado);
        existente.setDate(fechaSemana);
        existente.setHorasDiarias(2);

        when(asignacionRepo.findByUsuarioId(1L)).thenReturn(List.of(existente));
        when(asignacionRepo.save(asignacion)).thenReturn(asignacion);

        // Act
        Asignacion guardada = cronogramaService.guardarAsignacion(asignacion);

        // Assert
        assertTrue(guardada.getTieneConflicto(), "Debe detectar conflicto por solapamiento de turnos");
    }

    @Test
    void guardarAsignacion_detectaConflicto_cuandoExcedeDiezHorasDiarias() {
        // Arrange: creamos una asignación existente en otro turno pero sumando 8 horas
        Asignacion existente = new Asignacion();
        existente.setId(2L);
        existente.setUsuario(usuario);
        existente.setTurno(turnoTarde); // No solapa con turnoManana
        existente.setDate(fechaSemana);
        existente.setHorasDiarias(8); // 8 + 4 = 12 horas (> 10)

        when(asignacionRepo.findByUsuarioId(1L)).thenReturn(List.of(existente));
        when(asignacionRepo.save(asignacion)).thenReturn(asignacion);

        // Act
        Asignacion guardada = cronogramaService.guardarAsignacion(asignacion);

        // Assert
        assertTrue(guardada.getTieneConflicto(), "Debe detectar conflicto por exceder el límite de 10 horas diarias");
    }
}
