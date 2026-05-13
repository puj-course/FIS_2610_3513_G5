package com.studyhub.controller;

import com.studyhub.model.Usuario;
import com.studyhub.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller para la gestión del avatar (foto de perfil) de usuario.
 *
 * Endpoints expuestos:
 *   POST   /api/usuarios/{id}/avatar  — Sube o reemplaza el avatar del usuario.
 *   DELETE /api/usuarios/{id}/avatar  — Elimina el avatar y restaura el predeterminado.
 *
 * Las imágenes se sirven de forma estática desde /uploads/fotos-perfil/
 * gracias a la configuración de recursos estáticos en WebConfig.
 *
 * Nota: el campo fotoPerfil en la entidad Usuario almacena la URL relativa
 * del avatar. Un valor null indica que se usa el avatar predeterminado.
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class ImagenPerfilController {

    /** Tipos MIME aceptados según la HU: JPG, PNG, GIF */
    private static final List<String> TIPOS_PERMITIDOS = List.of(
            "image/jpeg",
            "image/png",
            "image/gif"
    );

    /** Tamaño máximo permitido: 5 MB en bytes */
    private static final long MAX_BYTES = 5L * 1024 * 1024;

    /**
     * Directorio local donde se persisten los avatares.
     * Configurable en application.properties con la clave app.upload.dir.
     */
    @Value("${app.upload.dir:uploads/fotos-perfil}")
    private String uploadDir;

    private final UsuarioRepository usuarioRepository;

    public ImagenPerfilController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // =========================================================================
    // POST /api/usuarios/{id}/avatar
    // =========================================================================

    /**
     * Sube o reemplaza el avatar de un usuario.
     *
     * Validaciones aplicadas (en orden):
     *  1. El usuario debe existir                         → 404 si no.
     *  2. El archivo no puede estar vacío                 → 400 si lo está.
     *  3. El tipo MIME debe ser image/jpeg, image/png
     *     o image/gif                                     → 400 si no lo es.
     *  4. El tamaño no puede superar los 5 MB             → 400 si lo supera.
     *
     * Flujo exitoso:
     *  - Si el usuario ya tenía un avatar previo, el archivo anterior
     *    se elimina del disco antes de guardar el nuevo.
     *  - El nuevo archivo se guarda con nombre único:
     *    usuario_{id}_{uuid}.{ext}
     *  - La URL pública resultante se persiste en fotoPerfil del usuario.
     *
     * @param id   ID del usuario propietario del avatar.
     * @param foto Archivo de imagen enviado como multipart/form-data (campo "foto").
     * @return 200 con { mensaje, urlAvatar } o código de error con { mensaje }.
     */
    @PostMapping(value = "/{id}/avatar", consumes = "multipart/form-data")
    public ResponseEntity<?> subirAvatar(
            @PathVariable Long id,
            @RequestParam("foto") MultipartFile foto) {

        // 1. Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Usuario no encontrado con ID: " + id));
        }

        // 2. Verificar que el archivo no está vacío
        if (foto == null || foto.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "No se recibió ningún archivo"));
        }

        // 3. Validar tipo MIME
        String contentType = foto.getContentType();
        if (contentType == null || !TIPOS_PERMITIDOS.contains(contentType)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje",
                            "Formato no permitido. Solo se aceptan imágenes JPG, PNG o GIF"));
        }

        // 4. Validar tamaño (máximo 5 MB)
        if (foto.getSize() > MAX_BYTES) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje",
                            "El archivo supera el tamaño máximo permitido de 5 MB"));
        }

        try {
            // 5. Crear el directorio de uploads si no existe
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            // 6. Eliminar avatar anterior si existe
            eliminarArchivoAnterior(usuario, uploadPath);

            // 7. Generar nombre único para el nuevo archivo
            String extension = resolverExtension(foto.getOriginalFilename(), contentType);
            String nombreArchivo = "usuario_" + id + "_" + UUID.randomUUID() + extension;

            // 8. Guardar el archivo en disco
            Path destino = uploadPath.resolve(nombreArchivo);
            Files.copy(foto.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            // 9. Persistir la URL pública en el usuario
            String urlPublica = "/uploads/fotos-perfil/" + nombreArchivo;
            usuario.setFotoPerfil(urlPublica);
            usuarioRepository.save(usuario);

            return ResponseEntity.ok(Map.of(
                    "mensaje",    "Avatar actualizado exitosamente",
                    "urlAvatar",  urlPublica
            ));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error al guardar el archivo: " + e.getMessage()));
        }
    }

    // =========================================================================
    // DELETE /api/usuarios/{id}/avatar
    // =========================================================================

    /**
     * Elimina el avatar de un usuario y restaura el avatar predeterminado.
     *
     * Comportamiento:
     *  - Si el usuario tiene un archivo de avatar en disco, se elimina físicamente.
     *  - El campo fotoPerfil del usuario se establece en null.
     *  - El frontend interpreta fotoPerfil == null como "mostrar avatar predeterminado".
     *
     * @param id ID del usuario cuyo avatar se quiere eliminar.
     * @return 200 con { mensaje } o código de error con { mensaje }.
     */
    @DeleteMapping("/{id}/avatar")
    public ResponseEntity<?> eliminarAvatar(@PathVariable Long id) {

        // 1. Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Usuario no encontrado con ID: " + id));
        }

        // 2. Si no tiene avatar, informar sin error (idempotente)
        if (usuario.getFotoPerfil() == null) {
            return ResponseEntity.ok(
                    Map.of("mensaje", "El usuario no tiene un avatar personalizado"));
        }

        try {
            // 3. Eliminar el archivo físico del disco
            Path uploadPath = Paths.get(uploadDir);
            eliminarArchivoAnterior(usuario, uploadPath);

            // 4. Limpiar el campo en BD → null = avatar predeterminado
            usuario.setFotoPerfil(null);
            usuarioRepository.save(usuario);

            return ResponseEntity.ok(
                    Map.of("mensaje", "Avatar eliminado. Se restauró el avatar predeterminado"));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error al eliminar el archivo: " + e.getMessage()));
        }
    }

    // =========================================================================
    // Métodos privados auxiliares
    // =========================================================================

    /**
     * Elimina del disco el archivo de avatar previo del usuario, si existe.
     * Extrae el nombre del archivo a partir de la URL relativa almacenada
     * en fotoPerfil (ej: "/uploads/fotos-perfil/usuario_1_uuid.jpg").
     *
     * @param usuario    Entidad del usuario.
     * @param uploadPath Ruta al directorio de uploads.
     * @throws IOException si ocurre un error de I/O al intentar borrar.
     */
    private void eliminarArchivoAnterior(Usuario usuario, Path uploadPath) throws IOException {
        if (usuario.getFotoPerfil() != null) {
            String nombreAnterior = usuario.getFotoPerfil()
                    .substring(usuario.getFotoPerfil().lastIndexOf('/') + 1);
            Path archivoAnterior = uploadPath.resolve(nombreAnterior);
            Files.deleteIfExists(archivoAnterior);
        }
    }

    /**
     * Resuelve la extensión de archivo apropiada.
     * Primero intenta extraerla del nombre original del archivo;
     * si no tiene extensión, la infiere del tipo MIME.
     *
     * @param nombreOriginal Nombre original del archivo subido (puede ser null).
     * @param contentType    Tipo MIME del archivo.
     * @return Extensión con punto (ej: ".jpg", ".png", ".gif").
     */
    private String resolverExtension(String nombreOriginal, String contentType) {
        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            return nombreOriginal.substring(nombreOriginal.lastIndexOf(".")).toLowerCase();
        }
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            default          -> ".jpg";
        };
    }
}