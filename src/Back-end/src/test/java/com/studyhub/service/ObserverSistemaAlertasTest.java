package com.studyhub.service;

import com.studyhub.model.Asignatura;
import com.studyhub.model.Nota;
import com.studyhub.model.Notification;
import com.studyhub.model.Usuario;
import com.studyhub.service.observer.NotaEventPublisher;
import com.studyhub.service.observer.NotaObserver;
import com.studyhub.service.observer.SistemaAlertasObserver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para el patrón ObserverSistemaAlertas.
 * Sin @SpringBootTest — objetos instanciados directamente con Mockito.
 */
class ObserverSistemaAlertasTest {

    // ── Helper ────────────────────────────────────────────────────────────────

    private Nota buildNota(Long userId, String nombreMateria) {
        Usuario usuario = new Usuario();
        usuario.setId(userId);

        Asignatura asignatura = new Asignatura();
        asignatura.setId(1L);
        asignatura.setNombre(nombreMateria);
        asignatura.setUsuario(usuario);

        Nota nota = new Nota("Parcial 1", 2.5, 30.0, asignatura);
        nota.setId(1L);
        return nota;
    }

    // ── NotaEventPublisher ────────────────────────────────────────────────────

    @Test
    @DisplayName("Publisher notifica a todos los observers registrados")
    void publisher_notificaTodosLosObservers() {
        NotaEventPublisher publisher = new NotaEventPublisher();

        List<String> llamadas = new ArrayList<>();
        NotaObserver obs1 = (n, ant, nue, uid, nom) -> llamadas.add("obs1");
        NotaObserver obs2 = (n, ant, nue, uid, nom) -> llamadas.add("obs2");

        publisher.registrar(obs1);
        publisher.registrar(obs2);

        publisher.notificar(buildNota(1L, "Cálculo"), 3.5, 2.8, 1L, "Cálculo");

        assertEquals(2, llamadas.size());
        assertTrue(llamadas.contains("obs1"));
        assertTrue(llamadas.contains("obs2"));
    }

    @Test
    @DisplayName("Publisher no registra el mismo observer dos veces")
    void publisher_noRegistraDuplicados() {
        NotaEventPublisher publisher = new NotaEventPublisher();

        List<String> llamadas = new ArrayList<>();
        NotaObserver obs = (n, ant, nue, uid, nom) -> llamadas.add("obs");

        publisher.registrar(obs);
        publisher.registrar(obs);

        publisher.notificar(buildNota(1L, "Física"), 3.0, 2.5, 1L, "Física");

        assertEquals(1, llamadas.size());
    }

    @Test
    @DisplayName("Publisher permite desregistrar un observer")
    void publisher_desregistraObserver() {
        NotaEventPublisher publisher = new NotaEventPublisher();

        List<String> llamadas = new ArrayList<>();
        NotaObserver obs = (n, ant, nue, uid, nom) -> llamadas.add("obs");

        publisher.registrar(obs);
        publisher.desregistrar(obs);

        publisher.notificar(buildNota(1L, "Física"), 3.0, 2.5, 1L, "Física");

        assertTrue(llamadas.isEmpty());
    }

    // ── SistemaAlertasObserver — alerta ───────────────────────────────────────

    @Test
    @DisplayName("NO dispara alerta si el promedio siempre estuvo por debajo de 3")
    void alerta_noDisparaSiSiempreEstuvoBajo() {
        NotaEventPublisher publisher = new NotaEventPublisher();
        NotificationService notificationService = mock(NotificationService.class);
        new SistemaAlertasObserver(publisher, notificationService);

        publisher.notificar(buildNota(1L, "Cálculo"), 2.5, 2.0, 1L, "Cálculo");

        verify(notificationService, never()).publicar(any(Notification.class));
    }

    @Test
    @DisplayName("NO dispara alerta si el promedio sigue por encima de 3")
    void alerta_noDisparaSiSiguePorEncima() {
        NotaEventPublisher publisher = new NotaEventPublisher();
        NotificationService notificationService = mock(NotificationService.class);
        new SistemaAlertasObserver(publisher, notificationService);

        publisher.notificar(buildNota(1L, "Cálculo"), 4.0, 3.2, 1L, "Cálculo");

        verify(notificationService, never()).publicar(any(Notification.class));
    }

    @Test
    @DisplayName("SÍ dispara alerta cuando el promedio cruza exactamente de 3.0 a <3")
    void alerta_disparaCuandoCruzaUmbralExacto() {
        NotaEventPublisher publisher = new NotaEventPublisher();
        NotificationService notificationService = mock(NotificationService.class);
        when(notificationService.publicar(any(Notification.class))).thenReturn(new Notification());
        new SistemaAlertasObserver(publisher, notificationService);

        publisher.notificar(buildNota(1L, "Cálculo"), 3.0, 2.9, 1L, "Cálculo");

        verify(notificationService, times(1)).publicar(any(Notification.class));
    }

    @Test
    @DisplayName("SÍ dispara alerta cuando el promedio baja bruscamente de 3.5 a 1.0")
    void alerta_disparaCuandoBajaBruscamente() {
        NotaEventPublisher publisher = new NotaEventPublisher();
        NotificationService notificationService = mock(NotificationService.class);
        when(notificationService.publicar(any(Notification.class))).thenReturn(new Notification());
        new SistemaAlertasObserver(publisher, notificationService);

        publisher.notificar(buildNota(2L, "Física"), 3.5, 1.0, 2L, "Física");

        verify(notificationService, times(1)).publicar(any(Notification.class));
    }

    // ── SistemaAlertasObserver — estadísticas ─────────────────────────────────

    @Test
    @DisplayName("Registra materia en riesgo cuando promedio < 3")
    void estadisticas_registraRiesgo() {
        NotaEventPublisher publisher = new NotaEventPublisher();
        NotificationService notificationService = mock(NotificationService.class);
        SistemaAlertasObserver observer = new SistemaAlertasObserver(publisher, notificationService);

        publisher.notificar(buildNota(1L, "Programación"), 3.5, 2.4, 1L, "Programación");

        assertTrue(observer.obtenerMateriasEnRiesgo(1L).contains("Programación"));
    }

    @Test
    @DisplayName("Retira materia de riesgo cuando promedio vuelve a >= 3")
    void estadisticas_retiraRiesgo() {
        NotaEventPublisher publisher = new NotaEventPublisher();
        NotificationService notificationService = mock(NotificationService.class);
        when(notificationService.publicar(any(Notification.class))).thenReturn(new Notification());
        SistemaAlertasObserver observer = new SistemaAlertasObserver(publisher, notificationService);

        publisher.notificar(buildNota(1L, "Programación"), 3.5, 2.4, 1L, "Programación");
        assertTrue(observer.obtenerMateriasEnRiesgo(1L).contains("Programación"));

        publisher.notificar(buildNota(1L, "Programación"), 2.4, 3.1, 1L, "Programación");
        assertFalse(observer.obtenerMateriasEnRiesgo(1L).contains("Programación"));
    }

    @Test
    @DisplayName("Devuelve conjunto vacío si el usuario no tiene materias en riesgo")
    void estadisticas_conjuntoVacioSinRiesgo() {
        NotaEventPublisher publisher = new NotaEventPublisher();
        NotificationService notificationService = mock(NotificationService.class);
        SistemaAlertasObserver observer = new SistemaAlertasObserver(publisher, notificationService);

        Set<String> enRiesgo = observer.obtenerMateriasEnRiesgo(99L);
        assertNotNull(enRiesgo);
        assertTrue(enRiesgo.isEmpty());
    }

    @Test
    @DisplayName("Aísla materias en riesgo por usuario")
    void estadisticas_aislaUsuarios() {
        NotaEventPublisher publisher = new NotaEventPublisher();
        NotificationService notificationService = mock(NotificationService.class);
        SistemaAlertasObserver observer = new SistemaAlertasObserver(publisher, notificationService);

        publisher.notificar(buildNota(1L, "Cálculo"), 3.5, 2.0, 1L, "Cálculo");
        publisher.notificar(buildNota(2L, "Historia"), 3.5, 2.0, 2L, "Historia");

        assertTrue(observer.obtenerMateriasEnRiesgo(1L).contains("Cálculo"));
        assertFalse(observer.obtenerMateriasEnRiesgo(1L).contains("Historia"));

        assertTrue(observer.obtenerMateriasEnRiesgo(2L).contains("Historia"));
        assertFalse(observer.obtenerMateriasEnRiesgo(2L).contains("Cálculo"));
    }
}