package com.studyhub.controller;

import com.studyhub.model.Usuario;
import com.studyhub.dto.*;
import com.studyhub.service.UsuarioService;
import com.studyhub.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmailService emailService;

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
            String loginAt = java.time.LocalDateTime.now().toString();
            return ResponseEntity.ok(Map.of(
                "id",       usuario.getId(),
                "nombre",   usuario.getNombre(),
                "apellido", usuario.getApellido(),
                "correo",   usuario.getCorreo(),
                "rol",      usuario.getRol(),
                "loginAt",  loginAt          // timestamp del login para el guard
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping
    public List<Usuario> obtenerUsuarios() {
        return usuarioService.obtenerTodos();
    }

    /**
     * Retorna los datos públicos de perfil de un usuario por su ID.
     * Se usa desde el frontend para precargar el formulario "Mi Perfil".
     *
     * Respuestas:
     *  - 200 OK       → datos del usuario
     *  - 404 Not Found → no existe un usuario con ese ID
     *
     * @param id ID del usuario
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Usuario u = usuarioService.obtenerPorId(id);
            return ResponseEntity.ok(u);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", e.getMessage()));
        }
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

    /**
     * Sube o reemplaza la foto de perfil de un usuario.
     *
     * Validaciones:
     *  - El archivo no puede estar vacío.
     *  - Solo se aceptan imágenes JPG, JPEG, PNG y WEBP.
     *  - El tamaño máximo permitido es 2 MB.
     *
     * El archivo se guarda en el sistema local bajo uploads/fotos-perfil/
     * y la URL pública resultante se persiste en el campo fotoPerfil del usuario.
     *
     * Respuestas:
     *  - 200 OK           → { url, fotoPerfil } con la URL pública de la imagen
     *  - 400 Bad Request  → archivo inválido (tipo o tamaño)
     *  - 404 Not Found    → usuario no encontrado
     *  - 500 Internal     → error al guardar el archivo
     *
     * @param id   ID del usuario
     * @param foto Archivo de imagen enviado como multipart/form-data (campo "foto")
     */
    @PostMapping("/{id}/foto")
    public ResponseEntity<?> subirFotoPerfil(
            @PathVariable Long id,
            @RequestParam("foto") MultipartFile foto) {
        try {
            String url = usuarioService.subirFotoPerfil(id, foto);
            return ResponseEntity.ok(Map.of(
                    "url",        url,
                    "fotoPerfil", url
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("mensaje", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PostMapping("/recuperar")
    public ResponseEntity<?> solicitarRecuperacion(@RequestParam String correo) {
        try {
            String token = usuarioService.generarTokenRecuperacion(correo);
            String baseUri = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            String enlace = baseUri + "/index.html?token=" + token;
            emailService.enviarCorreoRecuperacion(correo, enlace);
            return ResponseEntity.ok(Map.of("mensaje", "Enlace enviado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PostMapping("/restablecer")
    public ResponseEntity<?> restablecerPassword(@RequestBody RestablecerRequest request) {
        try {
            usuarioService.restablecerPassword(request.getToken(), request.getNuevaPassword());
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<EstadisticasDTO> obtenerEstadisticas(@PathVariable Long id) {
        EstadisticasDTO estadisticas = usuarioService.obtenerEstadisticas(id);
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/{id}/preferencias")
    public ResponseEntity<?> obtenerPreferencias(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioService.obtenerPreferencias(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PutMapping("/{id}/preferencias")
    public ResponseEntity<?> guardarPreferencias(@PathVariable Long id, @RequestBody Map<String, Object> preferencias) {
        try {
            usuarioService.guardarPreferencias(id, preferencias);
            return ResponseEntity.ok(Map.of("mensaje", "Preferencias guardadas exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
        }
    }
}