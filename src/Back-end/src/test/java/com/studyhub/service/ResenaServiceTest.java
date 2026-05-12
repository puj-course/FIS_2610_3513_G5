package com.studyhub.service;

import com.studyhub.model.Resena;
import com.studyhub.model.Usuario;
import com.studyhub.repository.ResenaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(TestResultLogger.class)
class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @InjectMocks
    private ResenaService resenaService;

    private Resena resena;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);

        resena = new Resena();
        resena.setComentario("Muy buena plataforma para organizar el semestre.");
        resena.setCalificacion(5);
        resena.setUsuario(usuario);
    }

    // - crearResena - CP normal -

    @Test
    void crearResena_guardaYRetornaResena_cuandoDatosValidos() {
        // Arrange
        when(resenaRepository.save(resena)).thenReturn(resena);

        // Act
        Resena resultado = resenaService.crearResena(resena);

        // Assert
        System.out.println("Resultado real: Resea creada - comentario='" + resultado.getComentario() + "', calificacion=" + resultado.getCalificacion());
        assertNotNull(resultado);
        assertEquals("Muy buena plataforma para organizar el semestre.", resultado.getComentario());
        verify(resenaRepository, times(1)).save(resena);
    }

    // - CP11: comentario vaco -

    @Test
    void crearResena_lanzaExcepcion_cuandoComentarioEsVacio() {
        // Arrange
        resena.setComentario("");

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> resenaService.crearResena(resena));

        System.out.println("Resultado real: Excepcin lanzada - \"" + ex.getMessage() + "\"");
        assertEquals("El comentario no puede estar vacío", ex.getMessage());
        verify(resenaRepository, never()).save(any());
    }

    @Test
    void crearResena_lanzaExcepcion_cuandoComentarioEsNulo() {
        // Arrange
        resena.setComentario(null);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> resenaService.crearResena(resena));

        System.out.println("Resultado real: Excepcin lanzada - \"" + ex.getMessage() + "\"");
        assertEquals("El comentario no puede estar vacío", ex.getMessage());
        verify(resenaRepository, never()).save(any());
    }

    @Test
    void crearResena_lanzaExcepcion_cuandoComentarioSoloTieneEspacios() {
        // Arrange
        resena.setComentario("   ");

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> resenaService.crearResena(resena));

        System.out.println("Resultado real: Excepcin lanzada - \"" + ex.getMessage() + "\"");
        assertEquals("El comentario no puede estar vacío", ex.getMessage());
        verify(resenaRepository, never()).save(any());
    }

    // - Borde: lmite de longitud del comentario -

    @Test
    void crearResena_lanzaExcepcion_cuandoComentarioSuperaLosQuinientosCaracteres() {
        // Arrange
        resena.setComentario("A".repeat(501));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> resenaService.crearResena(resena));

        System.out.println("Resultado real: Excepcin lanzada - \"" + ex.getMessage() + "\"");
        assertEquals("El comentario no puede superar los 500 caracteres", ex.getMessage());
        verify(resenaRepository, never()).save(any());
    }

    @Test
    void crearResena_guardaResena_cuandoComentarioTieneExactamenteQuinientosCaracteres() {
        // Arrange
        resena.setComentario("A".repeat(500));
        when(resenaRepository.save(resena)).thenReturn(resena);

        // Act
        Resena resultado = resenaService.crearResena(resena);

        // Assert
        System.out.println("Resultado real: Resea creada - longitud comentario=" + resultado.getComentario().length() + " caracteres");
        assertNotNull(resultado);
        verify(resenaRepository, times(1)).save(resena);
    }

    // - CP12: calificacin fuera de rango -

    @Test
    void crearResena_lanzaExcepcion_cuandoCalificacionEsMayorQueCinco() {
        // Arrange
        resena.setCalificacion(6);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> resenaService.crearResena(resena));

        System.out.println("Resultado real: Excepcin lanzada - \"" + ex.getMessage() + "\"");
        assertEquals("La calificación debe estar entre 1 y 5", ex.getMessage());
        verify(resenaRepository, never()).save(any());
    }

    @Test
    void crearResena_lanzaExcepcion_cuandoCalificacionEsMenorQueUno() {
        // Arrange
        resena.setCalificacion(0);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> resenaService.crearResena(resena));

        System.out.println("Resultado real: Excepcin lanzada - \"" + ex.getMessage() + "\"");
        assertEquals("La calificación debe estar entre 1 y 5", ex.getMessage());
        verify(resenaRepository, never()).save(any());
    }

    @Test
    void crearResena_guardaResena_cuandoCalificacionEsUno() {
        // Arrange
        resena.setCalificacion(1);
        when(resenaRepository.save(resena)).thenReturn(resena);

        // Act
        Resena resultado = resenaService.crearResena(resena);

        // Assert
        System.out.println("Resultado real: Resea creada - calificacion=" + resultado.getCalificacion());
        assertNotNull(resultado);
        verify(resenaRepository, times(1)).save(resena);
    }

    // - CP13: eliminarResena sin permiso -

    @Test
    void eliminarResena_lanzaExcepcion_cuandoUsuarioNoEsDueno() {
        // Arrange
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> resenaService.eliminarResena(1L, 2L));

        System.out.println("Resultado real: Excepcin lanzada - \"" + ex.getMessage() + "\"");
        assertEquals("No tienes permiso para eliminar esta reseña", ex.getMessage());
        verify(resenaRepository, never()).delete(any());
    }

    @Test
    void eliminarResena_eliminaCorrectamente_cuandoUsuarioEsDueno() {
        // Arrange
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));

        // Act
        assertDoesNotThrow(() -> resenaService.eliminarResena(1L, 1L));

        // Assert
        System.out.println("Resultado real: Resea eliminada correctamente - usuarioId=1, resenaId=1");
        verify(resenaRepository, times(1)).delete(resena);
    }

    @Test
    void eliminarResena_lanzaExcepcion_cuandoResenaNoExiste() {
        // Arrange
        when(resenaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> resenaService.eliminarResena(99L, 1L));

        System.out.println("Resultado real: Excepcin lanzada - \"" + ex.getMessage() + "\"");
        assertEquals("Reseña no encontrada", ex.getMessage());
        verify(resenaRepository, never()).delete(any());
    }
}