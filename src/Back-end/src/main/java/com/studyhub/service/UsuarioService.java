package com.studyhub.service;

import com.studyhub.dto.AsignaturaResumenDTO;
import com.studyhub.dto.ResumenAcademicoDTO;
import com.studyhub.dto.TareaResumenDTO;
import com.studyhub.dto.UsuarioResumenDTO;
import com.studyhub.dto.EstadisticasDTO;
import com.studyhub.model.Asignatura;
import com.studyhub.model.Tarea;
import com.studyhub.model.Usuario;
import com.studyhub.repository.TareaRepository;
import com.studyhub.repository.UsuarioRepository;
import com.studyhub.service.strategy.PasswordEncryptionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final AsignaturaService asignaturaService;
    private final NotaService notaService;
    private final TareaRepository tareaRepository;
    private final PasswordEncryptionStrategy encryptionStrategy;
    private final ObjectMapper objectMapper;

    @Autowired
    public UsuarioService(PasswordEncryptionStrategy encryptionStrategy,
            AsignaturaService asignaturaService,
            NotaService notaService,
            TareaRepository tareaRepository,
            ObjectMapper objectMapper) {
        this.encryptionStrategy = encryptionStrategy;
        this.asignaturaService = asignaturaService;
        this.notaService = notaService;
        this.tareaRepository = tareaRepository;
        this.objectMapper = objectMapper;
    }

    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }
        usuario.setPassword(encryptionStrategy.encrypt(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public Usuario login(String correo, String password) {
        return usuarioRepository.findByCorreo(correo)
                .filter(u -> encryptionStrategy.matches(password, u.getPassword()))
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
    }

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Busca un usuario por su ID y lo retorna.
     * Se usa para precargar el formulario "Mi Perfil" en el frontend.
     *
     * @param id ID del usuario
     * @return El usuario encontrado
     * @throws RuntimeException si no existe un usuario con ese ID (→ 404)
     */
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    /**
     * Directorio base donde se guardan las fotos de perfil (configurable en
     * application.properties)
     */
    @Value("${app.upload.dir:uploads/fotos-perfil}")
    private String uploadDir;

    /**
     * Sube la foto de perfil de un usuario, la guarda en disco y
     * actualiza el campo fotoPerfil con la URL pública resultante.
     *
     * Validaciones:
     * - El archivo no puede estar vacío.
     * - Solo se aceptan imágenes: image/jpeg, image/png, image/webp.
     * - El tamaño máximo es 2 MB.
     *
     * El archivo se renombra como {userId}_{uuid}.{ext} para evitar
     * colisiones y facilitar la identificación por usuario.
     *
     * @param id   ID del usuario propietario de la foto
     * @param foto Archivo recibido desde el frontend (multipart/form-data)
     * @return URL pública accesible de la imagen guardada
     * @throws IllegalArgumentException si el archivo es inválido
     * @throws RuntimeException         si el usuario no existe o falla el I/O
     */
    public String subirFotoPerfil(Long id, MultipartFile foto) {
        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Validar que el archivo no está vacío
        if (foto == null || foto.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen no puede estar vacío");
        }

        // Validar tipo MIME permitido
        String contentType = foto.getContentType();
        if (contentType == null ||
                (!contentType.equals("image/jpeg") &&
                        !contentType.equals("image/png") &&
                        !contentType.equals("image/webp"))) {
            throw new IllegalArgumentException("Solo se permiten imágenes JPG, PNG o WEBP");
        }

        // Validar tamaño máximo (2 MB)
        if (foto.getSize() > 2L * 1024 * 1024) {
            throw new IllegalArgumentException("La imagen no puede superar los 2 MB");
        }

        // Determinar extensión a partir del tipo MIME
        String extension = switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };

        // Nombre único: {userId}_{uuid}{ext}
        String nombreArchivo = id + "_" + UUID.randomUUID() + extension;

        try {
            // Crear directorio si no existe
            Path dirPath = Paths.get(uploadDir);
            Files.createDirectories(dirPath);

            // Guardar archivo (reemplaza si existía uno anterior con el mismo nombre)
            Path destino = dirPath.resolve(nombreArchivo);
            Files.copy(foto.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            // Construir URL pública: /uploads/fotos-perfil/{nombreArchivo}
            String urlPublica = "/uploads/fotos-perfil/" + nombreArchivo;

            // Persistir URL en la entidad
            usuario.setFotoPerfil(urlPublica);
            usuarioRepository.save(usuario);

            return urlPublica;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza los campos de perfil editables de un usuario:
     * nombre, apellido, carrera y semestre.
     *
     * Validaciones:
     * - El usuario debe existir (404 si no).
     * - nombre y apellido son obligatorios y no pueden estar vacíos (400).
     * - carrera y semestre son opcionales.
     *
     * @param id     ID del usuario a actualizar
     * @param campos Mapa con los campos: nombre, apellido, carrera, semestre
     * @return El usuario actualizado y persistido
     * @throws RuntimeException con mensaje descriptivo si falla la validación
     */
    public Usuario actualizarPerfil(Long id, Map<String, Object> campos) {
        // Buscar usuario — lanza excepción si no existe (se convierte en 404)
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Validar nombre (obligatorio)
        String nombre = campos.get("nombre") != null ? campos.get("nombre").toString().trim() : "";
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        // Validar apellido (obligatorio)
        String apellido = campos.get("apellido") != null ? campos.get("apellido").toString().trim() : "";
        if (apellido.isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }

        // Aplicar cambios obligatorios
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);

        // Aplicar campos opcionales si vienen en el body
        if (campos.containsKey("carrera")) {
            String carrera = campos.get("carrera") != null ? campos.get("carrera").toString().trim() : null;
            usuario.setCarrera(carrera.isEmpty() ? null : carrera);
        }

        if (campos.containsKey("semestre")) {
            try {
                Integer semestre = campos.get("semestre") != null
                        ? Integer.parseInt(campos.get("semestre").toString())
                        : null;
                if (semestre != null && (semestre < 1 || semestre > 12)) {
                    throw new IllegalArgumentException("El semestre debe estar entre 1 y 12");
                }
                usuario.setSemestre(semestre);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("El semestre debe ser un número entero válido");
            }
        }

        if (campos.containsKey("temaColor")) {
            String temaColor = campos.get("temaColor") != null ? campos.get("temaColor").toString().trim() : null;
            usuario.setTemaColor(temaColor == null || temaColor.isEmpty() ? null : temaColor);
        }

        return usuarioRepository.save(usuario);
    }

    public UsuarioResumenDTO obtenerResumenUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        List<Asignatura> asignaturas = asignaturaService.findByUserId(id);
        int totalAsignaturas = asignaturas.size();

        double sumaPromedios = 0.0;
        for (Asignatura asignatura : asignaturas) {
            double promedioAsignatura = notaService.calcularPromedio(asignatura.getId());
            sumaPromedios += promedioAsignatura;
        }

        double promedioGlobal = 0.0;
        if (totalAsignaturas > 0) {
            promedioGlobal = sumaPromedios / totalAsignaturas;
        }

        promedioGlobal = Math.round(promedioGlobal * 100.0) / 100.0;

        return new UsuarioResumenDTO(
                usuario.getId(),
                usuario.getNombre() + " " + (usuario.getApellido() != null ? usuario.getApellido() : ""),
                totalAsignaturas,
                promedioGlobal);
    }

    public ResumenAcademicoDTO obtenerResumenAcademico(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Asignatura> asignaturasRaw = asignaturaService.findByUserId(usuarioId);
        List<AsignaturaResumenDTO> asignaturasResumen = new ArrayList<>();
        double sumaPromedios = 0;

        for (Asignatura asig : asignaturasRaw) {
            double promedio = notaService.calcularPromedio(asig.getId());
            sumaPromedios += promedio;
            asignaturasResumen.add(new AsignaturaResumenDTO(
                    asig.getNombre(),
                    notaService.obtenerNotasPorAsignatura(asig.getId()),
                    promedio,
                    promedio < 3.0));
        }

        double promedioGlobal = asignaturasRaw.isEmpty() ? 0 : sumaPromedios / asignaturasRaw.size();
        promedioGlobal = Math.round(promedioGlobal * 100.0) / 100.0;

        List<Tarea> tareasRaw = tareaRepository
                .findByAsignatura_Usuario_IdAndEstadoTrueOrderByFechaEntregaAsc(usuarioId);
        List<TareaResumenDTO> tareasResumen = tareasRaw.stream()
                .map(t -> new TareaResumenDTO(t.getTitulo(), t.getAsignatura().getNombre(), t.getFechaEntrega(),
                        t.getHoraEntrega()))
                .collect(Collectors.toList());

        return new ResumenAcademicoDTO(
                usuario.getNombre() + " " + (usuario.getApellido() != null ? usuario.getApellido() : ""),
                promedioGlobal,
                asignaturasResumen,
                tareasResumen);
    }

    public String generarTokenRecuperacion(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("No existe un usuario registrado con ese correo"));

        String token = UUID.randomUUID().toString();
        usuario.setTokenRecuperacion(token);
        usuario.setTokenExpiracion(LocalDateTime.now().plusHours(1));
        usuarioRepository.save(usuario);
        return token;
    }

    public void restablecerPassword(String token, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findByTokenRecuperacion(token)
                .orElseThrow(() -> new RuntimeException("Token de recuperación inválido"));

        if (usuario.getTokenExpiracion().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El token ha expirado");
        }

        usuario.setPassword(encryptionStrategy.encrypt(nuevaPassword));
        usuario.setTokenRecuperacion(null);
        usuario.setTokenExpiracion(null);
        usuarioRepository.save(usuario);
    }

    public EstadisticasDTO obtenerEstadisticas(Long usuarioId) {
        List<Asignatura> asignaturas = asignaturaService.findByUserId(usuarioId);

        int totalMaterias = asignaturas.size();
        int totalCreditos = 0;
        int materiasEnRiesgo = 0;
        double sumaPromedios = 0.0;
        Map<String, Double> promediosPorMateria = new HashMap<>();

        for (Asignatura asig : asignaturas) {
            double promedio = notaService.calcularPromedio(asig.getId());
            promediosPorMateria.put(asig.getNombre(), promedio);

            sumaPromedios += promedio;
            totalCreditos += asig.getCreditos();

            if (promedio < 3.0 && promedio > 0) {
                materiasEnRiesgo++;
            }
        }

        double promedioGlobal = totalMaterias > 0 ? (sumaPromedios / totalMaterias) : 0.0;

        return new EstadisticasDTO(
                Math.round(promedioGlobal * 100.0) / 100.0,
                totalMaterias,
                materiasEnRiesgo,
                totalCreditos,
                promediosPorMateria);
    }

    public Map<String, Object> obtenerPreferencias(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        String prefs = usuario.getPreferencias();
        if (prefs == null || prefs.trim().isEmpty()) {
            return new HashMap<>(); // Preferencias por defecto
        }

        try {
            return objectMapper.readValue(prefs, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            System.err.println("Error al parsear preferencias JSON: " + e.getMessage());
            return new HashMap<>();
        }
    }

    public void guardarPreferencias(Long usuarioId, Map<String, Object> preferencias) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        try {
            String prefsJson = objectMapper.writeValueAsString(preferencias);
            usuario.setPreferencias(prefsJson);
            usuarioRepository.save(usuario);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al serializar preferencias JSON", e);
        }
    }
}