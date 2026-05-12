package com.studyhub.service;

import com.studyhub.model.Tarea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TestResultLogger.class)
class EstadoTareaServiceTest {

    private EstadoTareaService estadoTareaService;
    private Tarea tarea;

    @BeforeEach
    void setUp() {
        estadoTareaService = new EstadoTareaService();

        tarea = new Tarea();
        tarea.setTitulo("Tarea de prueba");
    }

    // - CP08: Tarea VENCIDA -

    @Test
    void obtenerEstado_retornaVencida_cuandoFechaEntregaEsAyer() {
        // Arrange
        tarea.setFechaEntrega(LocalDate.now().minusDays(1));

        // Act
        String estado = estadoTareaService.obtenerEstado(tarea);

        // Assert
        System.out.println("Resultado real: \"" + estado + "\"");
        assertEquals("VENCIDA", estado);
    }

    @Test
    void obtenerEstado_retornaVencida_cuandoFechaEntregaEsHaceMuchosTiempo() {
        // Arrange
        tarea.setFechaEntrega(LocalDate.now().minusDays(30));

        // Act
        String estado = estadoTareaService.obtenerEstado(tarea);

        // Assert
        System.out.println("Resultado real: \"" + estado + "\"");
        assertEquals("VENCIDA", estado);
    }

    // - CP09: Tarea PROXIMA -

    @Test
    void obtenerEstado_retornaProxima_cuandoFechaEntregaEsManana() {
        // Arrange
        tarea.setFechaEntrega(LocalDate.now().plusDays(1));

        // Act
        String estado = estadoTareaService.obtenerEstado(tarea);

        // Assert
        System.out.println("Resultado real: \"" + estado + "\"");
        assertEquals("PROXIMA", estado);
    }

    // - CP10: Tarea PENDIENTE -

    @Test
    void obtenerEstado_retornaPendiente_cuandoFechaEntregaEsEnDiezDias() {
        // Arrange
        tarea.setFechaEntrega(LocalDate.now().plusDays(10));

        // Act
        String estado = estadoTareaService.obtenerEstado(tarea);

        // Assert
        System.out.println("Resultado real: \"" + estado + "\"");
        assertEquals("PENDIENTE", estado);
    }

    @Test
    void obtenerEstado_retornaPendiente_cuandoFechaEntregaEsEnDosDias() {
        // Arrange
        tarea.setFechaEntrega(LocalDate.now().plusDays(2));

        // Act
        String estado = estadoTareaService.obtenerEstado(tarea);

        // Assert
        System.out.println("Resultado real: \"" + estado + "\"");
        assertEquals("PENDIENTE", estado);
    }

    // - Casos borde de la estrategia de proximidad -

    @Test
    void obtenerEstado_retornaPendiente_cuandoFechaEntregaEsHoy() {
        // Arrange
        tarea.setFechaEntrega(LocalDate.now());

        // Act
        String estado = estadoTareaService.obtenerEstado(tarea);

        // Assert
        System.out.println("Resultado real: \"" + estado + "\"");
        assertEquals("PENDIENTE", estado);
    }
}