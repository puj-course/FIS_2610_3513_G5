package com.studyhub.service;

import com.studyhub.dto.EstadisticasDTO;
import com.studyhub.dto.ResumenAcademicoDTO;
import com.studyhub.dto.UsuarioResumenDTO;
import com.studyhub.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Proxy de {@link IUsuarioService}.
 *
 * <p>Intercepta cada llamada que recibe un {@code usuarioId} y valida que sea
 * un valor positivo antes de delegar a {@link UsuarioService}.
 * Los métodos que no operan sobre un ID concreto (login, crearUsuario, etc.)
 * se delegan directamente sin validación adicional.</p>
 *
 * <p>Patrón GoF: <b>Proxy (Protection Proxy)</b></p>
 */
@Service("usuarioServiceProxy")
public class UsuarioServiceProxy implements IUsuarioService {

    private final IUsuarioService delegate;

    @Autowired
    public UsuarioServiceProxy(@Qualifier("usuarioService") IUsuarioService delegate) {
        this.delegate = delegate;
    }

    // ── Validación central ────────────────────────────────────────────────────

    /**
     * Verifica que el ID no sea nulo ni menor o igual a cero.
     *
     * @param id    valor a verificar
     * @param campo nombre del campo (para el mensaje de error)
     * @throws IllegalArgumentException si el ID es inválido
     */
    private void validarId(Long id, String campo) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "El " + campo + " debe ser un valor positivo; se recibió: " + id);
        }
    }

    // ── Métodos SIN validación de ID (delegación directa) ────────────────────

    @Override
    public String normalizarTelefono(String telefono) {
        return delegate.normalizarTelefono(telefono);
    }

    @Override
    public Usuario crearUsuario(Usuario usuario) {
        return delegate.crearUsuario(usuario);
    }

    @Override
    public Usuario login(String correo, String password) {
        return delegate.login(correo, password);
    }

    @Override
    public List<Usuario> obtenerTodos() {
        return delegate.obtenerTodos();
    }

    @Override
    public String generarTokenRecuperacion(String correo) {
        return delegate.generarTokenRecuperacion(correo);
    }

    @Override
    public String generarTokenRecuperacionPorTelefono(String telefonoRaw) {
        return delegate.generarTokenRecuperacionPorTelefono(telefonoRaw);
    }

    @Override
    public void restablecerPassword(String token, String nuevaPassword) {
        delegate.restablecerPassword(token, nuevaPassword);
    }

    // ── Métodos CON validación de ID ─────────────────────────────────────────

    @Override
    public Usuario obtenerPorId(Long id) {
        validarId(id, "usuarioId");
        return delegate.obtenerPorId(id);
    }

    @Override
    public Usuario actualizarPerfil(Long id, Map<String, Object> campos) {
        validarId(id, "usuarioId");
        return delegate.actualizarPerfil(id, campos);
    }

    @Override
    public UsuarioResumenDTO obtenerResumenUsuario(Long id) {
        validarId(id, "usuarioId");
        return delegate.obtenerResumenUsuario(id);
    }

    @Override
    public ResumenAcademicoDTO obtenerResumenAcademico(Long usuarioId) {
        validarId(usuarioId, "usuarioId");
        return delegate.obtenerResumenAcademico(usuarioId);
    }

    @Override
    public EstadisticasDTO obtenerEstadisticas(Long usuarioId) {
        validarId(usuarioId, "usuarioId");
        return delegate.obtenerEstadisticas(usuarioId);
    }

    @Override
    public Map<String, Object> obtenerPreferencias(Long usuarioId) {
        validarId(usuarioId, "usuarioId");
        return delegate.obtenerPreferencias(usuarioId);
    }

    @Override
    public void guardarPreferencias(Long usuarioId, Map<String, Object> preferencias) {
        validarId(usuarioId, "usuarioId");
        delegate.guardarPreferencias(usuarioId, preferencias);
    }

    @Override
    public void eliminarUsuario(Long id) {
        validarId(id, "usuarioId");
        delegate.eliminarUsuario(id);
    }
}