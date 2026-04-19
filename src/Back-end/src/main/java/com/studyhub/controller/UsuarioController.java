package com.studyhub.controller;

import com.studyhub.model.Usuario;
import com.studyhub.dto.UsuarioResumenDTO;
import com.studyhub.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            Usuario creado = usuarioService.crearUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        try {
            String correo   = credenciales.get("correo");
            String password = credenciales.get("password");
            Usuario usuario = usuarioService.login(correo, password);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping
    public List<Usuario> obtenerUsuarios() {
        return usuarioService.obtenerTodos();
    }

    @GetMapping("/{id}/resumen")
    public ResponseEntity<UsuarioResumenDTO> obtenerResumenUsuario(@PathVariable Long id) {
        try {
            UsuarioResumenDTO resumen = usuarioService.obtenerResumenUsuario(id);
            return ResponseEntity.ok(resumen);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}/informe-academico")
    public ResponseEntity<com.studyhub.dto.ResumenAcademicoDTO> obtenerInformeAcademico(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioService.obtenerResumenAcademico(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Actualiza los campos de perfil editables de un usuario.
     *
     * Campos aceptados en el body:
     *  - nombre    (String, obligatorio)
     *  - apellido  (String, obligatorio)
     *  - carrera   (String, opcional)
     *  - semestre  (Integer 1-12, opcional)
     *
     * Respuestas:
     *  - 200 OK           → usuario actualizado correctamente
     *  - 400 Bad Request  → campos obligatorios vacíos o semestre inválido
     *  - 404 Not Found    → no existe un usuario con ese ID
     *
     * @param id     ID del usuario a actualizar
     * @param campos Mapa con los campos del perfil
     * @return El usuario actualizado o un mensaje de error
     */
    @PutMapping("/{id}/perfil")
    public ResponseEntity<?> actualizarPerfil(@PathVariable Long id,
                                              @RequestBody Map<String, Object> campos) {
        try {
            Usuario actualizado = usuarioService.actualizarPerfil(id, campos);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Perfil actualizado exitosamente",
                    "usuario", actualizado
            ));
        } catch (IllegalArgumentException e) {
            // Validación fallida → 400
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        } catch (RuntimeException e) {
            // Usuario no encontrado → 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }
}