package com.studyhub.service;

import com.studyhub.model.Usuario;
import com.studyhub.repository.AsignaturaRepository;
import com.studyhub.repository.TareaRepository;
import com.studyhub.repository.UsuarioRepository;
import com.studyhub.service.strategy.PasswordEncryptionStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AsignaturaService asignaturaService;

    @Mock
    private NotaService notaService;

    @Mock
    private TareaRepository tareaRepository;

    @Mock
    private PasswordEncryptionStrategy encryptionStrategy;

    @Mock
    private ObjectMapper objectMapper;

    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(
                encryptionStrategy, asignaturaService, notaService,
                tareaRepository, objectMapper
        );
        // Se inyecta el mock del repository manualmente ya que usa @Autowired field injection
        try {
            var field = UsuarioService.class.getDeclaredField("usuarioRepository");
            field.setAccessible(true);
            field.set(usuarioService, usuarioRepository);
        } catch (Exception e) {
            throw new RuntimeException("Error inyectando mock de usuarioRepository", e);
        }

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Federico");
        usuario.setApellido("García");
        usuario.setCorreo("fede@studyhub.com");
        usuario.setPassword("plainPassword123");
        usuario.setRol("ESTUDIANTE");
    }

    // ─── CP01: Negativa — Crear usuario con correo duplicado ────────────────

    @Test
    void crearUsuario_lanzaExcepcion_cuandoCorreoYaExiste() {
        // Arrange
        when(usuarioRepository.existsByCorreo("fede@studyhub.com")).thenReturn(true);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.crearUsuario(usuario));

        assertEquals("El correo ya está registrado", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    // ─── CP02: Negativa — Login con credenciales inválidas ──────────────────

    @Test
    void login_lanzaExcepcion_cuandoPasswordIncorrecta() {
        // Arrange
        Usuario usuarioDB = new Usuario();
        usuarioDB.setCorreo("fede@studyhub.com");
        usuarioDB.setPassword("hashedPassword");

        when(usuarioRepository.findByCorreo("fede@studyhub.com"))
                .thenReturn(Optional.of(usuarioDB));
        when(encryptionStrategy.matches("wrongPassword", "hashedPassword"))
                .thenReturn(false);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.login("fede@studyhub.com", "wrongPassword"));

        assertEquals("Credenciales inválidas", ex.getMessage());
    }

    // ─── CP03: Borde — Actualizar perfil con nombre vacío ───────────────────

    @Test
    void actualizarPerfil_lanzaExcepcion_cuandoNombreEsVacio() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Map<String, Object> campos = new HashMap<>();
        campos.put("nombre", "");
        campos.put("apellido", "García");

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.actualizarPerfil(1L, campos));

        assertEquals("El nombre es obligatorio", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    // ─── CP04: Borde — Actualizar perfil con semestre = 12 (límite superior) ─

    @Test
    void actualizarPerfil_actualizaExitosamente_cuandoSemestreEsDoce() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Map<String, Object> campos = new HashMap<>();
        campos.put("nombre", "Federico");
        campos.put("apellido", "García");
        campos.put("semestre", "12");

        // Act
        Usuario resultado = usuarioService.actualizarPerfil(1L, campos);

        // Assert
        assertNotNull(resultado);
        assertEquals(12, resultado.getSemestre());
        assertEquals("Federico", resultado.getNombre());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    // ─── CP05: Lógica de negocio — Crear usuario exitosamente ───────────────

    @Test
    void crearUsuario_encriptaPasswordYGuarda_cuandoDatosValidos() {
        // Arrange
        when(usuarioRepository.existsByCorreo("fede@studyhub.com")).thenReturn(false);
        when(encryptionStrategy.encrypt("plainPassword123")).thenReturn("encryptedHash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Usuario resultado = usuarioService.crearUsuario(usuario);

        // Assert
        assertNotNull(resultado);
        assertEquals("encryptedHash", resultado.getPassword());
        verify(encryptionStrategy, times(1)).encrypt("plainPassword123");
        verify(usuarioRepository, times(1)).save(usuario);
    }

    // ─── CP06: Lógica — Obtener por ID ───────────────────────────────────────

    @Test
    void obtenerPorId_retornaUsuario_cuandoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        Usuario result = usuarioService.obtenerPorId(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void obtenerPorId_lanzaExcepcion_cuandoNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> usuarioService.obtenerPorId(99L));
    }

    // ─── CP07: Lógica — Resumen de Usuario ──────────────────────────────────

    @Test
    void obtenerResumenUsuario_calculaPromedioCorrectamente() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        
        com.studyhub.model.Asignatura a1 = new com.studyhub.model.Asignatura();
        a1.setId(10L); a1.setNombre("Materia 1");
        com.studyhub.model.Asignatura a2 = new com.studyhub.model.Asignatura();
        a2.setId(11L); a2.setNombre("Materia 2");
        
        when(asignaturaService.findByUserId(1L)).thenReturn(java.util.List.of(a1, a2));
        when(notaService.calcularPromedio(10L)).thenReturn(4.0);
        when(notaService.calcularPromedio(11L)).thenReturn(5.0);

        // Act
        var resumen = usuarioService.obtenerResumenUsuario(1L);

        // Assert
        assertEquals(4.5, resumen.getPromedioGlobal());
        assertEquals(2, resumen.getTotalAsignaturas());
    }

    // ─── CP08: Lógica — Estadísticas ────────────────────────────────────────

    @Test
    void obtenerEstadisticas_retornaDatosCorrectos() {
        // Arrange
        com.studyhub.model.Asignatura a1 = new com.studyhub.model.Asignatura();
        a1.setId(10L); a1.setNombre("M1"); a1.setCreditos(3);
        
        when(asignaturaService.findByUserId(1L)).thenReturn(java.util.List.of(a1));
        when(notaService.calcularPromedio(10L)).thenReturn(2.5); // Riesgo

        // Act
        var stats = usuarioService.obtenerEstadisticas(1L);

        // Assert
        assertEquals(1, stats.getMateriasEnRiesgo());
        assertEquals(3, stats.getTotalCreditos());
        assertEquals(2.5, stats.getPromedioGlobal());
    }

    // ─── CP09: Lógica — Recuperación de Contraseña ──────────────────────────

    @Test
    void generarTokenRecuperacion_guardaToken_cuandoCorreoExiste() {
        when(usuarioRepository.findByCorreo("fede@studyhub.com")).thenReturn(Optional.of(usuario));
        
        String token = usuarioService.generarTokenRecuperacion("fede@studyhub.com");
        
        assertNotNull(token);
        assertNotNull(usuario.getTokenRecuperacion());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void restablecerPassword_actualizaPasswordYBorraToken() {
        usuario.setTokenRecuperacion("valid-token");
        usuario.setTokenExpiracion(java.time.LocalDateTime.now().plusMinutes(10));
        
        when(usuarioRepository.findByTokenRecuperacion("valid-token")).thenReturn(Optional.of(usuario));
        when(encryptionStrategy.encrypt("newPass")).thenReturn("hashedNewPass");

        usuarioService.restablecerPassword("valid-token", "newPass");

        assertEquals("hashedNewPass", usuario.getPassword());
        assertNull(usuario.getTokenRecuperacion());
        verify(usuarioRepository).save(usuario);
    }

    // ─── CP10: Lógica — Preferencias ─────────────────────────────────────────

    @Test
    void obtenerPreferencias_retornaMapa_cuandoJsonEsValido() throws Exception {
        usuario.setPreferencias("{\"theme\":\"dark\"}");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        
        Map<String, Object> prefsMock = new HashMap<>();
        prefsMock.put("theme", "dark");
        when(objectMapper.readValue(eq("{\"theme\":\"dark\"}"), any(com.fasterxml.jackson.core.type.TypeReference.class)))
            .thenReturn(prefsMock);

        var result = usuarioService.obtenerPreferencias(1L);
        assertEquals("dark", result.get("theme"));
    }
}

