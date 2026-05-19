package com.studyhub.service;

import com.studyhub.model.Notification;
import com.studyhub.repository.NotificationRepository;
import com.studyhub.service.command.EliminarNotificacionCommand;
import com.studyhub.service.command.MarcarLeidaCommand;
import com.studyhub.service.command.NotificacionCommand;
import com.studyhub.service.command.NotificacionCommandInvoker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationCommandTest {

    @Mock
    private NotificationRepository repo;

    private NotificacionCommandInvoker invoker;

    // ── utilidad ──────────────────────────────────────────────────────────────

    private Notification notifConEstado(Long id, String status) {
        Notification n = new Notification();
        // Usamos reflexión para fijar el ID (campo privado sin setter)
        try {
            var field = Notification.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(n, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        n.setUserId(1L);
        n.setType("TEST");
        n.setMessage("Mensaje de prueba");
        n.setStatus(status);
        n.setPriority("NORMAL");
        return n;
    }

    @BeforeEach
    void setUp() {
        invoker = new NotificacionCommandInvoker();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // MarcarLeidaCommand
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void marcarLeida_ejecutar_cambiaStatusALeida() {
        Notification n = notifConEstado(10L, "NO_LEIDA");
        when(repo.findById(10L)).thenReturn(Optional.of(n));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        MarcarLeidaCommand cmd = new MarcarLeidaCommand(repo, 10L);
        cmd.ejecutar();

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(repo).save(captor.capture());
        assertEquals("LEIDA", captor.getValue().getStatus());
    }

    @Test
    void marcarLeida_deshacer_restauraEstadoPrevio() {
        Notification n = notifConEstado(10L, "NO_LEIDA");
        when(repo.findById(10L)).thenReturn(Optional.of(n));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        MarcarLeidaCommand cmd = new MarcarLeidaCommand(repo, 10L);
        cmd.ejecutar();  // captura estadoPrevio = "NO_LEIDA"
        cmd.deshacer();  // debe restaurar a "NO_LEIDA"

        // save se llama 2 veces: en ejecutar y en deshacer
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(repo, times(2)).save(captor.capture());
        assertEquals("NO_LEIDA", captor.getAllValues().get(1).getStatus());
    }

    @Test
    void marcarLeida_deshacer_sinEjecutarAntes_noHaceNada() {
        MarcarLeidaCommand cmd = new MarcarLeidaCommand(repo, 10L);
        cmd.deshacer();  // no debe lanzar excepción ni llamar al repo

        verify(repo, never()).findById(any());
        verify(repo, never()).save(any());
    }

    @Test
    void marcarLeida_ejecutar_lanzaExcepcion_siNotificacionNoExiste() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        MarcarLeidaCommand cmd = new MarcarLeidaCommand(repo, 99L);
        assertThrows(IllegalArgumentException.class, cmd::ejecutar);
    }

    @Test
    void marcarLeida_getDescripcion_noEsNula() {
        MarcarLeidaCommand cmd = new MarcarLeidaCommand(repo, 10L);
        assertNotNull(cmd.getDescripcion());
        assertFalse(cmd.getDescripcion().isBlank());
    }

    // ══════════════════════════════════════════════════════════════════════════
    // EliminarNotificacionCommand
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void eliminar_ejecutar_eliminaDelRepositorio() {
        Notification n = notifConEstado(20L, "NO_LEIDA");
        when(repo.findById(20L)).thenReturn(Optional.of(n));

        EliminarNotificacionCommand cmd = new EliminarNotificacionCommand(repo, 20L);
        cmd.ejecutar();

        verify(repo).deleteById(20L);
    }

    @Test
    void eliminar_ejecutar_guardaSnapshot() {
        Notification n = notifConEstado(20L, "NO_LEIDA");
        when(repo.findById(20L)).thenReturn(Optional.of(n));

        EliminarNotificacionCommand cmd = new EliminarNotificacionCommand(repo, 20L);
        cmd.ejecutar();

        assertNotNull(cmd.getSnapshot());
        assertEquals("Mensaje de prueba", cmd.getSnapshot().getMessage());
        assertEquals("NO_LEIDA", cmd.getSnapshot().getStatus());
    }

    @Test
    void eliminar_deshacer_restauraNotificacion() {
        Notification n = notifConEstado(20L, "NO_LEIDA");
        when(repo.findById(20L)).thenReturn(Optional.of(n));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        EliminarNotificacionCommand cmd = new EliminarNotificacionCommand(repo, 20L);
        cmd.ejecutar();
        cmd.deshacer();

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(repo).save(captor.capture());
        Notification restaurada = captor.getValue();
        assertEquals("Mensaje de prueba", restaurada.getMessage());
        assertEquals("NO_LEIDA",          restaurada.getStatus());
        assertNull(restaurada.getId());   // nueva entidad sin ID
    }

    @Test
    void eliminar_deshacer_sinEjecutarAntes_noHaceNada() {
        EliminarNotificacionCommand cmd = new EliminarNotificacionCommand(repo, 20L);
        cmd.deshacer();

        verify(repo, never()).save(any());
        verify(repo, never()).deleteById(any());
    }

    @Test
    void eliminar_ejecutar_lanzaExcepcion_siNotificacionNoExiste() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        EliminarNotificacionCommand cmd = new EliminarNotificacionCommand(repo, 99L);
        assertThrows(IllegalArgumentException.class, cmd::ejecutar);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // NotificacionCommandInvoker
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void invoker_ejecutar_yDeshacerUltimo_deshaceElUltimoComando() {
        Notification n = notifConEstado(10L, "NO_LEIDA");
        when(repo.findById(10L)).thenReturn(Optional.of(n));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        MarcarLeidaCommand cmd = new MarcarLeidaCommand(repo, 10L);
        invoker.ejecutar(1L, cmd);

        Optional<NotificacionCommand> deshecho = invoker.deshacerUltimo(1L);

        assertTrue(deshecho.isPresent());
        assertEquals("Notificación marcada como leída", deshecho.get().getDescripcion());
    }

    @Test
    void invoker_deshacerUltimo_sinHistorial_retornaEmpty() {
        Optional<NotificacionCommand> resultado = invoker.deshacerUltimo(99L);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void invoker_tieneHistorial_falso_cuandoPilaVacia() {
        assertFalse(invoker.tieneHistorial(42L));
    }

    @Test
    void invoker_tieneHistorial_verdadero_despuesDeEjecutar() {
        Notification n = notifConEstado(10L, "NO_LEIDA");
        when(repo.findById(10L)).thenReturn(Optional.of(n));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        invoker.ejecutar(1L, new MarcarLeidaCommand(repo, 10L));

        assertTrue(invoker.tieneHistorial(1L));
    }

    @Test
    void invoker_pilaEsIndependientePorUsuario() {
        Notification n1 = notifConEstado(10L, "NO_LEIDA");
        Notification n2 = notifConEstado(11L, "NO_LEIDA");

        when(repo.findById(10L)).thenReturn(Optional.of(n1));
        when(repo.findById(11L)).thenReturn(Optional.of(n2));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        invoker.ejecutar(1L, new MarcarLeidaCommand(repo, 10L));
        invoker.ejecutar(2L, new MarcarLeidaCommand(repo, 11L));

        assertTrue(invoker.tieneHistorial(1L));
        assertTrue(invoker.tieneHistorial(2L));

        invoker.deshacerUltimo(1L);

        assertFalse(invoker.tieneHistorial(1L));
        assertTrue(invoker.tieneHistorial(2L));   // usuario 2 no fue afectado
    }

    @Test
    void invoker_deshacerUltimo_vaciaPilaTrasDeshacerUnico() {
        Notification n = notifConEstado(10L, "NO_LEIDA");
        when(repo.findById(10L)).thenReturn(Optional.of(n));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        invoker.ejecutar(1L, new MarcarLeidaCommand(repo, 10L));
        invoker.deshacerUltimo(1L);

        assertFalse(invoker.tieneHistorial(1L));
    }
}