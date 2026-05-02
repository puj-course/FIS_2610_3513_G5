package com.studyhub.service;

import com.studyhub.model.Asignatura;
import com.studyhub.model.Nota;
import com.studyhub.repository.NotaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotaServiceTest {

    @Mock
    private NotaRepository notaRepository;

    @InjectMocks
    private NotaService notaService;

    private Asignatura asignatura;
    private Nota nota;

    @BeforeEach
    void setUp() {
        asignatura = new Asignatura();
        asignatura.setId(1L);

        nota = new Nota();
        nota.setNombre("Parcial 1");
        nota.setCalificacion(4.0);
        nota.setPorcentaje(30.0);
        nota.setAsignatura(asignatura);
    }

    // ─── agregarNota ────────────────────────────────────────────────────────

    @Test
    void agregarNota_guardaYRetornaNota_cuandoDatosValidos() {
        when(notaRepository.findByAsignaturaId(1L)).thenReturn(Collections.emptyList());
        when(notaRepository.save(nota)).thenReturn(nota);

        Nota resultado = notaService.agregarNota(nota);

        assertNotNull(resultado);
        assertEquals("Parcial 1", resultado.getNombre());
        verify(notaRepository, times(1)).save(nota);
    }

    @Test
    void agregarNota_lanzaExcepcion_cuandoPorcentajeEsNegativo() {
        nota.setPorcentaje(-10.0);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notaService.agregarNota(nota));

        assertEquals("El porcentaje debe estar entre 0 y 100", ex.getMessage());
        verify(notaRepository, never()).save(any());
    }

    @Test
    void agregarNota_lanzaExcepcion_cuandoPorcentajeSuperaCien() {
        nota.setPorcentaje(110.0);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notaService.agregarNota(nota));

        assertEquals("El porcentaje debe estar entre 0 y 100", ex.getMessage());
        verify(notaRepository, never()).save(any());
    }

    @Test
    void agregarNota_lanzaExcepcion_cuandoSumaSuperaCien() {
        Nota notaExistente = new Nota();
        notaExistente.setPorcentaje(80.0);
        notaExistente.setAsignatura(asignatura);

        when(notaRepository.findByAsignaturaId(1L)).thenReturn(List.of(notaExistente));

        nota.setPorcentaje(30.0);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notaService.agregarNota(nota));

        assertEquals("La suma de porcentajes no puede superar el 100%", ex.getMessage());
        verify(notaRepository, never()).save(any());
    }

    @Test
    void agregarNota_guardaNota_cuandoSumaExactamenteCien() {
        Nota notaExistente = new Nota();
        notaExistente.setPorcentaje(70.0);
        notaExistente.setAsignatura(asignatura);

        when(notaRepository.findByAsignaturaId(1L)).thenReturn(List.of(notaExistente));
        when(notaRepository.save(nota)).thenReturn(nota);

        nota.setPorcentaje(30.0);
        Nota resultado = notaService.agregarNota(nota);

        assertNotNull(resultado);
        verify(notaRepository, times(1)).save(nota);
    }

    // ─── obtenerTodasLasNotas ────────────────────────────────────────────────

    @Test
    void obtenerTodasLasNotas_retornaListaDeNotas() {
        when(notaRepository.findAll()).thenReturn(List.of(nota));

        List<Nota> resultado = notaService.obtenerTodasLasNotas();

        assertEquals(1, resultado.size());
        verify(notaRepository, times(1)).findAll();
    }

    @Test
    void obtenerTodasLasNotas_retornaListaVacia_cuandoNoHayNotas() {
        when(notaRepository.findAll()).thenReturn(Collections.emptyList());

        List<Nota> resultado = notaService.obtenerTodasLasNotas();

        assertTrue(resultado.isEmpty());
    }

    // ─── obtenerNotaPorId ───────────────────────────────────────────────────

    @Test
    void obtenerNotaPorId_retornaNota_cuandoExiste() {
        when(notaRepository.findById(1L)).thenReturn(Optional.of(nota));

        Optional<Nota> resultado = notaService.obtenerNotaPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Parcial 1", resultado.get().getNombre());
    }

    @Test
    void obtenerNotaPorId_retornaVacio_cuandoNoExiste() {
        when(notaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Nota> resultado = notaService.obtenerNotaPorId(99L);

        assertTrue(resultado.isEmpty());
    }

    // ─── obtenerNotasPorAsignatura ──────────────────────────────────────────

    @Test
    void obtenerNotasPorAsignatura_retornaNotas_cuandoExisten() {
        when(notaRepository.findByAsignaturaId(1L)).thenReturn(List.of(nota));

        List<Nota> resultado = notaService.obtenerNotasPorAsignatura(1L);

        assertEquals(1, resultado.size());
        verify(notaRepository, times(1)).findByAsignaturaId(1L);
    }

    // ─── actualizarNota ─────────────────────────────────────────────────────

    @Test
    void actualizarNota_retornaNotaActualizada_cuandoExiste() {
        Nota datosActualizados = new Nota();
        datosActualizados.setNombre("Parcial 2");
        datosActualizados.setCalificacion(4.5);
        datosActualizados.setPorcentaje(40.0);

        when(notaRepository.findById(1L)).thenReturn(Optional.of(nota));
        when(notaRepository.save(any(Nota.class))).thenAnswer(i -> i.getArgument(0));

        Optional<Nota> resultado = notaService.actualizarNota(1L, datosActualizados);

        assertTrue(resultado.isPresent());
        assertEquals("Parcial 2", resultado.get().getNombre());
        assertEquals(4.5, resultado.get().getCalificacion());
        assertEquals(40.0, resultado.get().getPorcentaje());
    }

    @Test
    void actualizarNota_retornaVacio_cuandoNoExiste() {
        when(notaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Nota> resultado = notaService.actualizarNota(99L, new Nota());

        assertTrue(resultado.isEmpty());
        verify(notaRepository, never()).save(any());
    }

    // ─── eliminarNota ───────────────────────────────────────────────────────

    @Test
    void eliminarNota_retornaTrue_cuandoExiste() {
        when(notaRepository.existsById(1L)).thenReturn(true);

        boolean resultado = notaService.eliminarNota(1L);

        assertTrue(resultado);
        verify(notaRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarNota_retornaFalse_cuandoNoExiste() {
        when(notaRepository.existsById(99L)).thenReturn(false);

        boolean resultado = notaService.eliminarNota(99L);

        assertFalse(resultado);
        verify(notaRepository, never()).deleteById(any());
    }

    // ─── calcularPromedio ───────────────────────────────────────────────────

    @Test
    void calcularPromedio_retornaCero_cuandoNoHayNotas() {
        when(notaRepository.findByAsignaturaId(1L)).thenReturn(Collections.emptyList());

        double resultado = notaService.calcularPromedio(1L);

        assertEquals(0.0, resultado);
    }

    @Test
    void calcularPromedio_calculaCorrectamente_conUnaNote() {
        // 4.0 * (30 / 100) = 1.2
        when(notaRepository.findByAsignaturaId(1L)).thenReturn(List.of(nota));

        double resultado = notaService.calcularPromedio(1L);

        assertEquals(1.2, resultado, 0.001);
    }

    @Test
    void calcularPromedio_calculaCorrectamente_conVariasNotas() {
        // Parcial 1: 4.0 * (30/100) = 1.2
        // Parcial 2: 3.5 * (70/100) = 2.45
        // Total esperado: 3.65
        Nota nota2 = new Nota();
        nota2.setNombre("Parcial 2");
        nota2.setCalificacion(3.5);
        nota2.setPorcentaje(70.0);
        nota2.setAsignatura(asignatura);

        when(notaRepository.findByAsignaturaId(1L)).thenReturn(List.of(nota, nota2));

        double resultado = notaService.calcularPromedio(1L);

        assertEquals(3.65, resultado, 0.001);
    }

    @Test
    void calcularPromedio_retornaCinco_cuandoCalificacionMaximaYPorcentajeCompleto() {
        nota.setCalificacion(5.0);
        nota.setPorcentaje(100.0);

        when(notaRepository.findByAsignaturaId(1L)).thenReturn(List.of(nota));

        double resultado = notaService.calcularPromedio(1L);

        assertEquals(5.0, resultado, 0.001);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Pruebas adicionales — Taller Pruebas y Métricas (CP11 – CP15)
    // ═══════════════════════════════════════════════════════════════════════════

    // ─── CP11: Borde — Agregar nota con porcentaje = 0 ──────────────────────

    @Test
    void agregarNota_guardaNota_cuandoPorcentajeEsCero() {
        // Arrange
        nota.setPorcentaje(0.0);
        when(notaRepository.findByAsignaturaId(1L)).thenReturn(Collections.emptyList());
        when(notaRepository.save(nota)).thenReturn(nota);

        // Act
        Nota resultado = notaService.agregarNota(nota);

        // Assert
        assertNotNull(resultado);
        assertEquals(0.0, resultado.getPorcentaje());
        verify(notaRepository, times(1)).save(nota);
    }

    // ─── CP12: Borde — Agregar nota con porcentaje = 100 (lista vacía) ──────

    @Test
    void agregarNota_guardaNota_cuandoPorcentajeEsCienYNoHayOtrasNotas() {
        // Arrange
        nota.setPorcentaje(100.0);
        when(notaRepository.findByAsignaturaId(1L)).thenReturn(Collections.emptyList());
        when(notaRepository.save(nota)).thenReturn(nota);

        // Act
        Nota resultado = notaService.agregarNota(nota);

        // Assert
        assertNotNull(resultado);
        assertEquals(100.0, resultado.getPorcentaje());
        verify(notaRepository, times(1)).save(nota);
    }

    // ─── CP13: Negativa — Calcular promedio con calificación null ────────────

    @Test
    void calcularPromedio_ignoraNotasSinCalificacion_cuandoCalificacionEsNull() {
        // Arrange
        Nota notaSinCalificacion = new Nota();
        notaSinCalificacion.setNombre("Quiz");
        notaSinCalificacion.setCalificacion(null);
        notaSinCalificacion.setPorcentaje(20.0);
        notaSinCalificacion.setAsignatura(asignatura);

        when(notaRepository.findByAsignaturaId(1L))
                .thenReturn(List.of(nota, notaSinCalificacion));

        // Act — nota tiene 4.0 * (30/100) = 1.2, la otra se ignora
        double resultado = notaService.calcularPromedio(1L);

        // Assert
        assertEquals(1.2, resultado, 0.001);
    }

    // ─── CP14: Negativa — Calcular progreso sin notas ───────────────────────

    @Test
    void calcularProgreso_retornaCero_cuandoNoHayNotas() {
        // Arrange
        when(notaRepository.findByAsignaturaId(1L)).thenReturn(Collections.emptyList());

        // Act
        double resultado = notaService.calcularProgreso(1L);

        // Assert
        assertEquals(0.0, resultado);
    }

    // ─── CP15: Lógica de negocio — Calcular progreso con notas mixtas ───────

    @Test
    void calcularProgreso_sumaSoloPorcentajesDeNotasCalificadas_cuandoHayNotasMixtas() {
        // Arrange
        // nota ya tiene calificacion = 4.0, porcentaje = 30.0

        Nota notaCalificada = new Nota();
        notaCalificada.setNombre("Parcial 2");
        notaCalificada.setCalificacion(3.5);
        notaCalificada.setPorcentaje(40.0);
        notaCalificada.setAsignatura(asignatura);

        Nota notaSinCalificar = new Nota();
        notaSinCalificar.setNombre("Final");
        notaSinCalificar.setCalificacion(null);
        notaSinCalificar.setPorcentaje(30.0);
        notaSinCalificar.setAsignatura(asignatura);

        when(notaRepository.findByAsignaturaId(1L))
                .thenReturn(List.of(nota, notaCalificada, notaSinCalificar));

        // Act — Solo debe sumar 30 + 40 = 70 (ignora la nota sin calificación)
        double resultado = notaService.calcularProgreso(1L);

        // Assert
        assertEquals(70.0, resultado, 0.001);
    }
}
