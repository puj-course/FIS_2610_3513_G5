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
 * Controller para la gestión de fotos de perfil de usuario.
 *
 * Endpoint principal:
 * ST /api/usuarios/{id}/foto — ecibe imagen, valida y guarda en disco
 *
 * Las imágenes se sirven de forma estática desde /uploads/fotos-perfil/
 * gracias a la configuración de recursos estáticos en WebConfig.
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class ImagenPerfilController {

    /** Tipos MIME permitidos */
    private static final List<String> TIPOS_PERMITIDOS = List.of("image/jpeg", "image/png", "image/webp");

    /** Tamaño máximo: 2 MB en bytes */
    private static final long MAX_BYTES = 2 * 1024 * 1024;

    @Value("${app.upload.dir:uploads/fotos-perfil}")
    private String uploadDir;

    private final UsuarioRepository usuarioRepository;

    public ImagenPerfilController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Recibe una imagen de perfil, la valida y la guarda en disco.
     *
     * Validaciones:
     *  El usuario debe existir (404).
     *  El archivo no puede estar vacío (400).
     *  Solo se aceptan JPG, PNG y WebP (400).
     *  El tamaño máximo es 2 MB (400).
     *
     * @param id   D del usuario
     * @param foto rchivo de imagen enviado como multipart/form-data
     * @return URL pública de la imagen guardada
     */
    @PostMapping(value = "/{id}/foto", consumes = "multipart/form-data")
    public ResponseEntity<?> subirFotoPerfil(
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

        // 3. Validar tipo de archivo
        String contentType = foto.getContentType();
        if (contentType == null || !TIPOS_PERMITIDOS.contains(contentType)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "Tipo de archivo no permitido. Solo se aceptan JPG, PNG y WebP"));
        }

        // 4. Validar tamaño
        if (foto.getSize() > MAX_BYTES) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "El archivo supera el tamaño máximo permitido de 2 MB"));
        }

        try {
            // 5. Crear el directorio de uploads si no existe
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            // 6. Generar nombre único para el archivo
            String extension = obtenerExtension(foto.getOriginalFilename(), contentType);
            String nombreArchivo = "usuario_" + id + "_" + UUID.randomUUID() + extension;

            // 7. Eliminar foto anterior si existe
            if (usuario.getFotoPerfil() != null) {
                String nombreAnterior = usuario.getFotoPerfil()
                        .substring(usuario.getFotoPerfil().lastIndexOf('/') + 1);
                Path archivoAnterior = uploadPath.resolve(nombreAnterior);
                Files.deleteIfExists(archivoAnterior);
            }

            // 8. Guardar el archivo en disco
            Path destino = uploadPath.resolve(nombreArchivo);
            Files.copy(foto.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            // 9. Construir URL pública y persistir en BD
            String urlPublica = "/uploads/fotos-perfil/" + nombreArchivo;
            usuario.setFotoPerfil(urlPublica);
            usuarioRepository.save(usuario);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Foto de perfil actualizada exitosamente",
                    "urlFoto", urlPublica));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error al guardar el archivo: " + e.getMessage()));
        }
    }

    /**
    * Infiere la extensión del archivo a partir del nombre original.
    * Si no tiene extensión, usa el Content-Type como alternativa.
    */
    private String obtenerExtension(String nombreOriginal, String contentType) {
        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            return nombreOriginal.substring(nombreOriginal.lastIndexOf(".")).toLowerCase();
        }
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        }; 
    }
}