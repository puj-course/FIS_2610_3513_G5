package com.studyhub.service;

import com.studyhub.model.Notification;
import com.studyhub.service.factory.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NotificacionFactoryTest {

    @Test
    void testTareaPendiente() {
        NotificacionFactory factory = new NotificacionTareaPendienteFactory();
        Notification n = factory.crear(1L, "Parcial de Cálculo", "2025-06-01");

        assertEquals("TAREA", n.getType());
        assertEquals("CRITICA", n.getPriority());
        assertTrue(n.getMessage().contains("Parcial de Cálculo"));
    }

    @Test
    void testClaseProxima() {
        NotificacionFactory factory = new NotificacionClaseProximaFactory();
        Notification n = factory.crear(1L, "Física", "10:00");

        assertEquals("CALENDARIO", n.getType());
        assertEquals("NORMAL", n.getPriority());
        assertTrue(n.getMessage().contains("Física"));
    }

    @Test
    void testMateriaEnRiesgo() {
        NotificacionFactory factory = new NotificacionMateriaEnRiesgoFactory();
        Notification n = factory.crear(1L, "Álgebra", 2.8);

        assertEquals("CALIFICACION", n.getType());
        assertEquals("CRITICA", n.getPriority());
        assertTrue(n.getMessage().contains("Álgebra"));
    }

    @Test
    void testSistema() {
        NotificacionFactory factory = new NotificacionSistemaFactory();
        Notification n = factory.crear(1L, "Mantenimiento programado");

        assertEquals("SISTEMA", n.getType());
        assertEquals("NORMAL", n.getPriority());
        assertEquals("Mantenimiento programado", n.getMessage());
    }
}