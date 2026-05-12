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
@ExtendWith(TestResultLogger.class)
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

    // - agregarNota -

    // CP01 - Normal: datos vlidos
    @Test
    void agregarNota_guardaYRetornaNota_cuandoDatosValidos() {
        // Arrange
        when(notaRepository.findByAsignaturaId(1L)).thenReturn(Collections.emptyList());
        when(notaRepository.save(nota)).thenReturn(nota);

        // Act
        Nota resultado = notaService.agregarNota(nota);

        // Assert
        System.out.println("Resultado real: Nota guardada - nombre='" + resultado.getNombre() + "', porcentaje=" + resultado.getPorcentaje() + "%");
        assertNotNull(resultado);
        assertEquals("Parcial 1", resultado.getNombre());
        verify(notaRepository, times(1)).save(nota);
    }

    // CP02 - Negativa: porcentaje negativo
    @Test
    void agregarNota_lanzaExcepcion_cuandoPorcentajeEsNegativo() {
        // Arrange
        nota.setPorcentaje(-10.0);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notaService.agregarNota(nota));

        System.out.println("Resultado real: Excepcin lanzada - \"" + ex.getMessage() + "\"");
        assertEquals("El porcentaje debe estar entre 0 y 100", ex.getMessage());
        verify(notaRepository, never()).save(any());
    }

    // CP03 - Negativa: suma acumulada supera 100%
    @Test
    void agregarNota_lanzaExcepcion_cuandoSumaSuperaCien() {
        // Arrange
        Nota notaExistente = new Nota();
        notaExistente.setPorcentaje(80.0);
        notaExistente.setAsignatura(asignatura);

        when(notaRepository.findByAsignaturaId(1L)).thenReturn(List.of(notaExistente));
        nota.setPorcentaje(30.0);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notaService.agregarNota(nota));

        System.out.println("Resultado real: Excepcin lanzada - \"" + ex.getMessage() + "\"");
        assertEquals("La suma de porcentajes no puede superar el 100%", ex.getMessage());
        verify(notaRepository, never()).save(any());
    }

    // CP04 - Borde: una sola nota que ocupa exactamente el 100% desde cero
    @Test
    void agregarNota_guardaNota_cuandoPorcentajeEsExactamenteCien() {
        // Arrange
        nota.setPorcentaje(100.0);
        when(notaRepository.findByAsignaturaId(1L)).thenReturn(Collections.emptyList());
        when(notaRepository.save(nota)).thenReturn(nota);

        // Act
        Nota resultado = notaService.agregarNota(nota);

        // Assert
        System.out.println("Resultado real: Nota guardada - porcentaje=" + resultado.getPorcentaje() + "%");
        assertNotNull(resultado);
        verify(notaRepository, times(1)).save(nota);
    }

    // CP05 - Borde: porcentaje un punto sobre el lmite superior
    @Test
    void agregarNota_lanzaExcepcion_cuandoPorcentajeEsCientoUno() {
        // Arrange
        nota.setPorcentaje(101.0);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notaService.agregarNota(nota));

        System.out.println("Resultado real: Excepcin lanzada - \"" + ex.getMessage() + "\"");
        assertEquals("El porcentaje debe estar entre 0 y 100", ex.getMessage());
        verify(notaRepository, never()).save(any());
    }

    // Borde adicional: suma de dos notas llega exactamente a 100%
    @Test
    void agregarNota_guardaNota_cuandoSumaLlegaExactamenteCien() {
        // Arrange
        Nota notaExistente = new Nota();
        notaExistente.setPorcentaje(70.0);
        notaExistente.setAsignatura(asignatura);

        when(notaRepository.findByAsignaturaId(1L)).thenReturn(List.of(notaExistente));
        when(notaRepository.save(nota)).thenReturn(nota);
        nota.setPorcentaje(30.0);

        // Act
        Nota resultado = notaService.agregarNota(nota);

        // Assert
        System.out.println("Resultado real: Nota guardada - suma acumulada=100%, porcentaje nueva nota=" + resultado.getPorcentaje() + "%");
        assertNotNull(resultado);
        verify(notaRepository, times(1)).save(nota);
    }

    // - obtenerTodasLasNotas -

    @Test
    void obtenerTodasLasNotas_retornaListaDeNotas() {
        when(notaRepository.findAll()).thenReturn(List.of(nota));

        List<Nota> resultado = notaService.obtenerTodasLasNotas();

        System.out.println("Resultado real: Lista retornada con " + resultado.size() + " nota(s)");
        assertEquals(1, resultado.size());
        verify(notaRepository, times(1)).findAll();
    }

    @Test
    void obtenerTodasLasNotas_retornaListaVacia_cuandoNoHayNotas() {
        when(notaRepository.findAll()).thenReturn(Collections.emptyList());

        List<Nota> resultado = notaService.obtenerTodasLasNotas();

        System.out.println("Resultado real: Lista vaca retornada - size=" + resultado.size());
        assertTrue(resultado.isEmpty());
    }

    // - obtenerNotaPorId -

    @Test
    void obtenerNotaPorId_retornaNota_cuandoExiste() {
        when(notaRepository.findById(1L)).thenReturn(Optional.of(nota));

        Optional<Nota> resultado = notaService.obtenerNotaPorId(1L);

        System.out.println("Resultado real: Nota encontrada - nombre='" + resultado.get().getNombre() + "'");
        assertTrue(resultado.isPresent());
        assertEquals("Parcial 1", resultado.get().getNombre());
    }

    @Test
    void obtenerNotaPorId_retornaVacio_cuandoNoExiste() {
        when(notaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Nota> resultado = notaService.obtenerNotaPorId(99L);

        System.out.println("Resultado real: Optional vaco - presente=" + resultado.isPresent());
        assertTrue(resultado.isEmpty());
    }

    // - obtenerNotasPorAsignatura -

    @Test
    void obtenerNotasPorAsignatura_retornaNotas_cuandoExisten() {
        when(notaRepository.findByAsignaturaId(1L)).thenReturn(List.of(nota));

        List<Nota> resultado = notaService.obtenerNotasPorAsignatura(1L);

        System.out.println("Resultado real: Lista retornada con " + resultado.size() + " nota(s) para asignaturaId=1");
        assertEquals(1, resultado.size());
        verify(notaRepository, times(1)).findByAsignaturaId(1L);
    }

    // - actualizarNota -

    @Test
    void actualizarNota_retornaNotaActualizada_cuandoExiste() {
        // Arrange
        Nota datosActualizados = new Nota();
        datosActualizados.setNombre("Parcial 2");
        datosActualizados.setCalificacion(4.5);
        datosActualizados.setPorcentaje(40.0);

        when(notaRepository.findById(1L)).thenReturn(Optional.of(nota));
        when(notaRepository.save(any(Nota.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Optional<Nota> resultado = notaService.actualizarNota(1L, datosActualizados);

        // Assert
        System.out.println("Resultado real: Nota actualizada - nombre='" + resultado.get().getNombre() + "', calificacion=" + resultado.get().getCalificacion() + ", porcentaje=" + resultado.get().getPorcentaje() + "%");
        assertTrue(resultado.isPresent());
        assertEquals("Parcial 2", resultado.get().getNombre());
        assertEquals(4.5, resultado.get().getCalificacion());
        assertEquals(40.0, resultado.get().getPorcentaje());
    }

    @Test
    void actualizarNota_retornaVacio_cuandoNoExiste() {
        when(notaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Nota> resultado = notaService.actualizarNota(99L, new Nota());

        System.out.println("Resultado real: Optional vaco - presente=" + resultado.isPresent());
        assertTrue(resultado.isEmpty());
        verify(notaRepository, never()).save(any());
    }

    // - eliminarNota -

    @Test
    void eliminarNota_retornaTrue_cuandoExiste() {
        when(notaRepository.existsById(1L)).thenReturn(true);

        boolean resultado = notaService.eliminarNota(1L);

        System.out.println("Resultado real: " + resultado + " - nota con id=1 eliminada correctamente");
        assertTrue(resultado);
        verify(notaRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarNota_retornaFalse_cuandoNoExiste() {
        when(notaRepository.existsById(99L)).thenReturn(false);

        boolean resultado = notaService.eliminarNota(99L);

        System.out.println("Resultado real: " + resultado + " - nota con id=99 no encontrada");
        assertFalse(resultado);
        verify(notaRepository, never()).deleteById(any());
    }

    // - calcularPromedio -

    // CP07 - Borde: lista vaca
    @Test
    void calcularPromedio_retornaCero_cuandoNoHayNotas() {
        // Arrange
        when(notaRepository.findByAsignaturaId(1L)).thenReturn(Collections.emptyList());

        // Act
        double resultado = notaService.calcularPromedio(1L);

        // Assert
        System.out.println("Resultado real: " + resultado);
        assertEquals(0.0, resultado);
    }

    // CP06 - Normal: promedio ponderado con una nota
    @Test
    void calcularPromedio_calculaCorrectamente_conUnaNota() {
        // Arrange - 4.0 * (30 / 100) = 1.2
        when(notaRepository.findByAsignaturaId(1L)).thenReturn(List.of(nota));

        // Act
        double resultado = notaService.calcularPromedio(1L);

        // Assert
        System.out.println("Resultado real: " + resultado);
        assertEquals(1.2, resultado, 0.001);
    }

    // CP06 - Normal: promedio ponderado con varias notas
    @Test
    void calcularPromedio_calculaCorrectamente_conVariasNotas() {
        // Arrange - 4.0*(30/100) + 3.5*(70/100) = 1.2 + 2.45 = 3.65
        Nota nota2 = new Nota();
        nota2.setNombre("Parcial 2");
        nota2.setCalificacion(3.5);
        nota2.setPorcentaje(70.0);
        nota2.setAsignatura(asignatura);

        when(notaRepository.findByAsignaturaId(1L)).thenReturn(List.of(nota, nota2));

        // Act
        double resultado = notaService.calcularPromedio(1L);

        // Assert
        System.out.println("Resultado real: " + resultado);
        assertEquals(3.65, resultado, 0.001);
    }

    // Borde: calificacin mxima con porcentaje completo
    @Test
    void calcularPromedio_retornaCinco_cuandoCalificacionMaximaYPorcentajeCompleto() {
        // Arrange
        nota.setCalificacion(5.0);
        nota.setPorcentaje(100.0);

        when(notaRepository.findByAsignaturaId(1L)).thenReturn(List.of(nota));

        // Act
        double resultado = notaService.calcularPromedio(1L);

        // Assert
        System.out.println("Resultado real: " + resultado);
        assertEquals(5.0, resultado, 0.001);
    }

    // Negativa: nota sin calificacin asignada debe omitirse
    @Test
    void calcularPromedio_ignoraNotas_cuandoCalificacionEsNula() {
        // Arrange
        nota.setCalificacion(null);
        when(notaRepository.findByAsignaturaId(1L)).thenReturn(List.of(nota));

        // Act
        double resultado = notaService.calcularPromedio(1L);

        // Assert
        System.out.println("Resultado real: " + resultado);
        assertEquals(0.0, resultado, 0.001);
    }

    // - calcularProgreso -

    @Test
    void calcularProgreso_retornaCero_cuandoNoHayNotas() {
        // Arrange
        when(notaRepository.findByAsignaturaId(1L)).thenReturn(Collections.emptyList());

        // Act
        double resultado = notaService.calcularProgreso(1L);

        // Assert
        System.out.println("Resultado real: " + resultado + "%");
        assertEquals(0.0, resultado);
    }

    @Test
    void calcularProgreso_retornaSumaDePorcentajes_cuandoHayNotasConCalificacion() {
        // Arrange
        Nota nota2 = new Nota();
        nota2.setCalificacion(3.0);
        nota2.setPorcentaje(70.0);
        nota2.setAsignatura(asignatura);

        when(notaRepository.findByAsignaturaId(1L)).thenReturn(List.of(nota, nota2));

        // Act
        double resultado = notaService.calcularProgreso(1L);

        // Assert
        System.out.println("Resultado real: " + resultado + "%");
        assertEquals(100.0, resultado, 0.001);
    }

    @Test
    void calcularProgreso_ignoraNotas_cuandoCalificacionEsNula() {
        // Arrange
        nota.setCalificacion(null);
        when(notaRepository.findByAsignaturaId(1L)).thenReturn(List.of(nota));

        // Act
        double resultado = notaService.calcularProgreso(1L);

        // Assert
        System.out.println("Resultado real: " + resultado + "%");
        assertEquals(0.0, resultado, 0.001);
    }
}