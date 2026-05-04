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
}
