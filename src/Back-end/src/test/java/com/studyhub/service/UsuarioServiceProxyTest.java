package com.studyhub.service;

import com.studyhub.dto.EstadisticasDTO;
import com.studyhub.dto.ResumenAcademicoDTO;
import com.studyhub.dto.UsuarioResumenDTO;
import com.studyhub.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para {@link UsuarioServiceProxy}.
 * Sin Spring context — usa Mockito puro para verificar:
 *  1. IDs válidos → se delega al servicio real.
 *  2. IDs inválidos (null, 0, negativo) → se lanza IllegalArgumentException sin llamar al delegado.
 */
class UsuarioServiceProxyTest {

    private IUsuarioService delegate;
    private UsuarioServiceProxy proxy;

    @BeforeEach
    void setUp() {
        delegate = mock(IUsuarioService.class);
        proxy = new UsuarioServiceProxy(delegate);
    }

    // ── obtenerPorId ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorId: ID válido → delega correctamente")
    void obtenerPorId_idValido_delega() {
        Usuario mockUsuario = new Usuario();
        mockUsuario.setId(1L);
        when(delegate.obtenerPorId(1L)).thenReturn(mockUsuario);

        Usuario resultado = proxy.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(delegate, times(1)).obtenerPorId(1L);
    }

    @Test
    @DisplayName("obtenerPorId: ID null → IllegalArgumentException, sin delegar")
    void obtenerPorId_idNull_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> proxy.obtenerPorId(null));
        verify(delegate, never()).obtenerPorId(any());
    }

    @Test
    @DisplayName("obtenerPorId: ID cero → IllegalArgumentException, sin delegar")
    void obtenerPorId_idCero_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> proxy.obtenerPorId(0L));
        verify(delegate, never()).obtenerPorId(any());
    }

    @Test
    @DisplayName("obtenerPorId: ID negativo → IllegalArgumentException, sin delegar")
    void obtenerPorId_idNegativo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> proxy.obtenerPorId(-5L));
        verify(delegate, never()).obtenerPorId(any());
    }

    // ── actualizarPerfil ─────────────────────────────────────────────────────

    @Test
    @DisplayName("actualizarPerfil: ID válido → delega correctamente")
    void actualizarPerfil_idValido_delega() {
        Map<String, Object> campos = new HashMap<>();
        campos.put("nombre", "Ana");
        campos.put("apellido", "García");
        Usuario mockUsuario = new Usuario();
        when(delegate.actualizarPerfil(2L, campos)).thenReturn(mockUsuario);

        proxy.actualizarPerfil(2L, campos);

        verify(delegate, times(1)).actualizarPerfil(2L, campos);
    }

    @Test
    @DisplayName("actualizarPerfil: ID null → IllegalArgumentException, sin delegar")
    void actualizarPerfil_idNull_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> proxy.actualizarPerfil(null, new HashMap<>()));
        verify(delegate, never()).actualizarPerfil(any(), any());
    }

    // ── obtenerResumenUsuario ────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerResumenUsuario: ID válido → delega correctamente")
    void obtenerResumenUsuario_idValido_delega() {
        UsuarioResumenDTO dto = new UsuarioResumenDTO(1L, "Ana García", 3, 4.2);
        when(delegate.obtenerResumenUsuario(1L)).thenReturn(dto);

        UsuarioResumenDTO resultado = proxy.obtenerResumenUsuario(1L);

        assertNotNull(resultado);
        verify(delegate, times(1)).obtenerResumenUsuario(1L);
    }

    @Test
    @DisplayName("obtenerResumenUsuario: ID negativo → IllegalArgumentException, sin delegar")
    void obtenerResumenUsuario_idNegativo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> proxy.obtenerResumenUsuario(-1L));
        verify(delegate, never()).obtenerResumenUsuario(any());
    }

    // ── obtenerEstadisticas ──────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerEstadisticas: ID válido → delega correctamente")
    void obtenerEstadisticas_idValido_delega() {
        EstadisticasDTO dto = new EstadisticasDTO(3.8, 4, 1, 16, new HashMap<>());
        when(delegate.obtenerEstadisticas(3L)).thenReturn(dto);

        proxy.obtenerEstadisticas(3L);

        verify(delegate, times(1)).obtenerEstadisticas(3L);
    }

    @Test
    @DisplayName("obtenerEstadisticas: ID cero → IllegalArgumentException, sin delegar")
    void obtenerEstadisticas_idCero_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> proxy.obtenerEstadisticas(0L));
        verify(delegate, never()).obtenerEstadisticas(any());
    }

    // ── eliminarUsuario ──────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarUsuario: ID válido → delega correctamente")
    void eliminarUsuario_idValido_delega() {
        doNothing().when(delegate).eliminarUsuario(1L);

        proxy.eliminarUsuario(1L);

        verify(delegate, times(1)).eliminarUsuario(1L);
    }

    @Test
    @DisplayName("eliminarUsuario: ID null → IllegalArgumentException, sin delegar")
    void eliminarUsuario_idNull_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> proxy.eliminarUsuario(null));
        verify(delegate, never()).eliminarUsuario(any());
    }

    // ── Métodos sin validación de ID (delegación directa) ───────────────────

    @Test
    @DisplayName("login: siempre delega sin validación de ID")
    void login_delegaDirectamente() {
        Usuario mockUsuario = new Usuario();
        when(delegate.login("test@mail.com", "pass123")).thenReturn(mockUsuario);

        proxy.login("test@mail.com", "pass123");

        verify(delegate, times(1)).login("test@mail.com", "pass123");
    }

    @Test
    @DisplayName("obtenerTodos: siempre delega sin validación de ID")
    void obtenerTodos_delegaDirectamente() {
        when(delegate.obtenerTodos()).thenReturn(List.of());

        proxy.obtenerTodos();

        verify(delegate, times(1)).obtenerTodos();
    }

    // ── Mensaje de error descriptivo ─────────────────────────────────────────

    @Test
    @DisplayName("Mensaje de error incluye el valor inválido recibido")
    void mensajeError_incluyeValorInvalido() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> proxy.obtenerPorId(-99L));
        assertTrue(ex.getMessage().contains("-99"),
                "El mensaje debe mencionar el valor recibido: " + ex.getMessage());
    }
}