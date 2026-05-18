package com.studyhub.service;

import com.studyhub.model.Asignatura;
import com.studyhub.model.Tarea;
import com.studyhub.model.Usuario;
import com.studyhub.repository.TareaRepository;
import com.studyhub.repository.UsuarioRepository;
import com.studyhub.service.strategy.PasswordEncryptionStrategy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @Test
    void crearUsuario_lanzaExcepcion_cuandoCorreoYaExiste() {
        when(usuarioRepository.existsByCorreo("fede@studyhub.com")).thenReturn(true);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.crearUsuario(usuario));
        assertEquals("El correo ya está registrado", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void crearUsuario_encriptaPasswordYGuarda_cuandoDatosValidos() {
        when(usuarioRepository.existsByCorreo("fede@studyhub.com")).thenReturn(false);
        when(encryptionStrategy.encrypt("plainPassword123")).thenReturn("encryptedHash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario resultado = usuarioService.crearUsuario(usuario);

        assertNotNull(resultado);
        assertEquals("encryptedHash", resultado.getPassword());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void login_lanzaExcepcion_cuandoPasswordIncorrecta() {
        Usuario usuarioDB = new Usuario();
        usuarioDB.setCorreo("fede@studyhub.com");
        usuarioDB.setPassword("hashedPassword");

        when(usuarioRepository.findByCorreo("fede@studyhub.com")).thenReturn(Optional.of(usuarioDB));
        when(encryptionStrategy.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.login("fede@studyhub.com", "wrongPassword"));
        assertEquals("Credenciales inválidas", ex.getMessage());
    }

    @Test
    void login_retornaUsuario_cuandoCredencialesSonValidas() {
        Usuario usuarioDB = new Usuario();
        usuarioDB.setCorreo("fede@studyhub.com");
        usuarioDB.setPassword("hashedPassword");

        when(usuarioRepository.findByCorreo("fede@studyhub.com")).thenReturn(Optional.of(usuarioDB));
        when(encryptionStrategy.matches("correctPassword", "hashedPassword")).thenReturn(true);

        Usuario res = usuarioService.login("fede@studyhub.com", "correctPassword");
        assertNotNull(res);
    }

    @Test
    void obtenerTodos_retornaListaDeUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));
        List<Usuario> res = usuarioService.obtenerTodos();
        assertEquals(1, res.size());
    }

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

    @Test
    void actualizarPerfil_lanzaExcepcion_cuandoNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        Map<String, Object> campos = new HashMap<>();
        assertThrows(RuntimeException.class, () -> usuarioService.actualizarPerfil(99L, campos));
    }

    @Test
    void actualizarPerfil_lanzaExcepcion_cuandoNombreEsVacio() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        Map<String, Object> campos = Map.of("nombre", "");
        assertThrows(IllegalArgumentException.class, () -> usuarioService.actualizarPerfil(1L, campos));
    }

    @Test
    void actualizarPerfil_lanzaExcepcion_cuandoApellidoEsVacio() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        Map<String, Object> campos = Map.of("nombre", "Federico", "apellido", "");
        assertThrows(IllegalArgumentException.class, () -> usuarioService.actualizarPerfil(1L, campos));
    }

    @Test
    void actualizarPerfil_actualizaCamposOpcionales_cuandoSonValidos() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Map<String, Object> campos = new HashMap<>();
        campos.put("nombre", "Federico");
        campos.put("apellido", "García");
        campos.put("carrera", "Ingeniería");
        campos.put("semestre", "10");
        campos.put("temaColor", "dark");
        campos.put("fotoPerfil", "http://foto.jpg");

        Usuario resultado = usuarioService.actualizarPerfil(1L, campos);
        assertEquals("Ingeniería", resultado.getCarrera());
        assertEquals(10, resultado.getSemestre());
        assertEquals("dark", resultado.getTemaColor());
        assertEquals("http://foto.jpg", resultado.getFotoPerfil());
    }

    @Test
    void actualizarPerfil_limpiaCamposOpcionales_cuandoVienenVacios() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Map<String, Object> campos = new HashMap<>();
        campos.put("nombre", "Federico");
        campos.put("apellido", "García");
        campos.put("carrera", "");
        campos.put("temaColor", "");
        campos.put("fotoPerfil", "");

        Usuario resultado = usuarioService.actualizarPerfil(1L, campos);
        assertNull(resultado.getCarrera());
        assertNull(resultado.getTemaColor());
        assertNull(resultado.getFotoPerfil());
    }

    @Test
    void actualizarPerfil_lanzaExcepcion_cuandoSemestreEsInvalido() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        Map<String, Object> campos = Map.of("nombre", "F", "apellido", "G", "semestre", "15");
        assertThrows(IllegalArgumentException.class, () -> usuarioService.actualizarPerfil(1L, campos));

        Map<String, Object> camposErr = Map.of("nombre", "F", "apellido", "G", "semestre", "xyz");
        assertThrows(IllegalArgumentException.class, () -> usuarioService.actualizarPerfil(1L, camposErr));
    }

    @Test
    void obtenerResumenUsuario_calculaPromedioCorrectamente_cuandoHayAsignaturas() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Asignatura a1 = new Asignatura();
        a1.setId(10L);
        a1.setNombre("Materia 1");

        Asignatura a2 = new Asignatura();
        a2.setId(11L);
        a2.setNombre("Materia 2");

        when(asignaturaService.findByUserId(1L)).thenReturn(List.of(a1, a2));
        when(notaService.calcularPromedio(10L)).thenReturn(4.0);
        when(notaService.calcularPromedio(11L)).thenReturn(5.0);

        var res = usuarioService.obtenerResumenUsuario(1L);
        assertEquals(4.5, res.getPromedioGlobal());
        assertEquals(2, res.getTotalAsignaturas());
        assertEquals("Federico García", res.getNombre());
    }

    @Test
    void obtenerResumenUsuario_calculaPromedioCero_cuandoNoHayAsignaturas() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(asignaturaService.findByUserId(1L)).thenReturn(Collections.emptyList());

        var res = usuarioService.obtenerResumenUsuario(1L);
        assertEquals(0.0, res.getPromedioGlobal());
        assertEquals(0, res.getTotalAsignaturas());
    }

    @Test
    void obtenerResumenUsuario_lanzaExcepcion_cuandoUsuarioNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> usuarioService.obtenerResumenUsuario(99L));
    }

    @Test
    void obtenerResumenAcademico_retornaDatosCompletos() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Asignatura a1 = new Asignatura();
        a1.setId(10L);
        a1.setNombre("Matemáticas");

        when(asignaturaService.findByUserId(1L)).thenReturn(List.of(a1));
        when(notaService.calcularPromedio(10L)).thenReturn(2.5); // en riesgo < 3.0
        when(notaService.obtenerNotasPorAsignatura(10L)).thenReturn(Collections.emptyList());

        Tarea t1 = new Tarea();
        t1.setTitulo("Taller 1");
        t1.setAsignatura(a1);
        t1.setFechaEntrega(LocalDate.now().plusDays(1));
        t1.setHoraEntrega(LocalTime.of(14, 0));

        when(tareaRepository.findByAsignatura_Usuario_IdAndEstadoTrueOrderByFechaEntregaAsc(1L))
                .thenReturn(List.of(t1));

        var resumen = usuarioService.obtenerResumenAcademico(1L);

        assertEquals("Federico García", resumen.getNombreUsuario());
        assertEquals(2.5, resumen.getPromedioGlobal());
        assertEquals(1, resumen.getAsignaturas().size());
        assertTrue(resumen.getAsignaturas().get(0).isEnRiesgo());
        assertEquals(1, resumen.getTareasPendientes().size());
        assertEquals("Taller 1", resumen.getTareasPendientes().get(0).getTitulo());
    }

    @Test
    void obtenerResumenAcademico_lanzaExcepcion_cuandoUsuarioNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> usuarioService.obtenerResumenAcademico(99L));
    }

    @Test
    void generarTokenRecuperacion_guardaToken_cuandoCorreoExiste() {
        when(usuarioRepository.findByCorreo("fede@studyhub.com")).thenReturn(Optional.of(usuario));

        String token = usuarioService.generarTokenRecuperacion("fede@studyhub.com");

        assertNotNull(token);
        assertNotNull(usuario.getTokenRecuperacion());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void generarTokenRecuperacion_lanzaExcepcion_cuandoCorreoNoExiste() {
        when(usuarioRepository.findByCorreo("no@existe.com")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> usuarioService.generarTokenRecuperacion("no@existe.com"));
    }

    @Test
    void restablecerPassword_actualizaPassword_cuandoTokenEsValido() {
        usuario.setTokenRecuperacion("token123");
        usuario.setTokenExpiracion(LocalDateTime.now().plusHours(1));

        when(usuarioRepository.findByTokenRecuperacion("token123")).thenReturn(Optional.of(usuario));
        when(encryptionStrategy.encrypt("nuevaClave")).thenReturn("hashNuevaClave");

        usuarioService.restablecerPassword("token123", "nuevaClave");

        assertEquals("hashNuevaClave", usuario.getPassword());
        assertNull(usuario.getTokenRecuperacion());
        assertNull(usuario.getTokenExpiracion());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void restablecerPassword_lanzaExcepcion_cuandoTokenInvalido() {
        when(usuarioRepository.findByTokenRecuperacion("invalid")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> usuarioService.restablecerPassword("invalid", "new"));
    }

    @Test
    void restablecerPassword_lanzaExcepcion_cuandoTokenExpiro() {
        usuario.setTokenExpiracion(LocalDateTime.now().minusHours(1));
        when(usuarioRepository.findByTokenRecuperacion("expired")).thenReturn(Optional.of(usuario));
        assertThrows(RuntimeException.class, () -> usuarioService.restablecerPassword("expired", "new"));
    }

    @Test
    void obtenerEstadisticas_calculaDatosCorrectamente_cuandoHayMaterias() {
        Asignatura a1 = new Asignatura();
        a1.setId(10L);
        a1.setNombre("M1");
        a1.setCreditos(3);

        Asignatura a2 = new Asignatura();
        a2.setId(11L);
        a2.setNombre("M2");
        a2.setCreditos(4);

        when(asignaturaService.findByUserId(1L)).thenReturn(List.of(a1, a2));
        when(notaService.calcularPromedio(10L)).thenReturn(2.5); // Riesgo < 3.0
        when(notaService.calcularPromedio(11L)).thenReturn(4.5); // Sin riesgo

        var stats = usuarioService.obtenerEstadisticas(1L);

        assertEquals(3.5, stats.getPromedioGlobal());
        assertEquals(2, stats.getTotalMaterias());
        assertEquals(1, stats.getMateriasEnRiesgo());
        assertEquals(7, stats.getTotalCreditos());
        assertEquals(2.5, stats.getPromediosPorMateria().get("M1"));
    }

    @Test
    void obtenerEstadisticas_calculaCero_cuandoNoHayMaterias() {
        when(asignaturaService.findByUserId(1L)).thenReturn(Collections.emptyList());
        var stats = usuarioService.obtenerEstadisticas(1L);
        assertEquals(0.0, stats.getPromedioGlobal());
        assertEquals(0, stats.getTotalMaterias());
    }

    @Test
    void obtenerPreferencias_retornaMapa_cuandoJsonEsValido() throws Exception {
        usuario.setPreferencias("{\"theme\":\"dark\"}");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Map<String, Object> mockMap = Map.of("theme", "dark");
        when(objectMapper.readValue(eq("{\"theme\":\"dark\"}"), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                .thenReturn(mockMap);

        var res = usuarioService.obtenerPreferencias(1L);
        assertEquals("dark", res.get("theme"));
    }

    @Test
    void obtenerPreferencias_retornaVacio_cuandoPreferenciasEsNulo() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        usuario.setPreferencias(null);
        var res = usuarioService.obtenerPreferencias(1L);
        assertTrue(res.isEmpty());
    }

    @Test
    void obtenerPreferencias_retornaVacio_cuandoLanzaExcepcionJson() throws Exception {
        usuario.setPreferencias("invalid-json");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(objectMapper.readValue(eq("invalid-json"), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                .thenThrow(JsonProcessingException.class);

        var res = usuarioService.obtenerPreferencias(1L);
        assertTrue(res.isEmpty());
    }

    @Test
    void guardarPreferencias_guardaExitosamente() throws Exception {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(objectMapper.writeValueAsString(anyMap())).thenReturn("{\"theme\":\"light\"}");

        usuarioService.guardarPreferencias(1L, Map.of("theme", "light"));
        assertEquals("{\"theme\":\"light\"}", usuario.getPreferencias());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void guardarPreferencias_lanzaExcepcion_cuandoFallaJson() throws Exception {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(objectMapper.writeValueAsString(anyMap())).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class, () -> usuarioService.guardarPreferencias(1L, Map.of()));
    }
}
