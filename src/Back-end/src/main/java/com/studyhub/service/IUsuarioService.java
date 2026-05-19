package com.studyhub.service;

import com.studyhub.dto.EstadisticasDTO;
import com.studyhub.dto.ResumenAcademicoDTO;
import com.studyhub.dto.UsuarioResumenDTO;
import com.studyhub.model.Usuario;

import java.util.List;
import java.util.Map;

/**
 * Interfaz del servicio de usuarios.
 * Implementada por {@link UsuarioService} (lógica real)
 * y por {@link UsuarioServiceProxy} (validación de ID antes de delegar).
 */
public interface IUsuarioService {

    String normalizarTelefono(String telefono);

    Usuario crearUsuario(Usuario usuario);

    Usuario login(String correo, String password);

    List<Usuario> obtenerTodos();

    Usuario obtenerPorId(Long id);

    Usuario actualizarPerfil(Long id, Map<String, Object> campos);

    UsuarioResumenDTO obtenerResumenUsuario(Long id);

    ResumenAcademicoDTO obtenerResumenAcademico(Long usuarioId);

    String generarTokenRecuperacion(String correo);

    String generarTokenRecuperacionPorTelefono(String telefonoRaw);

    void restablecerPassword(String token, String nuevaPassword);

    EstadisticasDTO obtenerEstadisticas(Long usuarioId);

    Map<String, Object> obtenerPreferencias(Long usuarioId);

    void guardarPreferencias(Long usuarioId, Map<String, Object> preferencias);

    void eliminarUsuario(Long id);
}