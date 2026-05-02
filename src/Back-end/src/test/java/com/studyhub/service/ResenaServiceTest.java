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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
        usuario.setNombre("Federico");

        resena = new Resena();
        resena.setId(1L);
        resena.setComentario("Excelente materia, muy buen contenido");
        resena.setCalificacion(5);
        resena.setTipo("MATERIA");
        resena.setObjetivo("Fundamentos de Ingeniería de Software");
        resena.setUsuario(usuario);
    }

    // ─── CP06: Negativa — Crear reseña con comentario vacío ─────────────────

    @Test
    void crearResena_lanzaExcepcion_cuandoComentarioEsNull() {
        // Arrange
        resena.setComentario(null);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> resenaService.crearResena(resena));

        assertEquals("El comentario no puede estar vacío", ex.getMessage());
        verify(resenaRepository, never()).save(any());
    }

    // ─── CP07: Negativa — Eliminar reseña de otro usuario ───────────────────

    @Test
    void eliminarResena_lanzaExcepcion_cuandoUsuarioNoEsDueno() {
        // Arrange
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));

        Long otroUsuarioId = 99L; // Un usuario diferente al dueño (ID=1)

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> resenaService.eliminarResena(1L, otroUsuarioId));

        assertEquals("No tienes permiso para eliminar esta reseña", ex.getMessage());
        verify(resenaRepository, never()).delete(any());
    }

    // ─── CP08: Borde — Crear reseña con exactamente 500 caracteres ──────────

    @Test
    void crearResena_guardaExitosamente_cuandoComentarioTiene500Chars() {
        // Arrange
        String comentario500 = "a".repeat(500);
        resena.setComentario(comentario500);
        resena.setCalificacion(4);

        when(resenaRepository.save(any(Resena.class))).thenReturn(resena);

        // Act
        Resena resultado = resenaService.crearResena(resena);

        // Assert
        assertNotNull(resultado);
        assertEquals(500, resultado.getComentario().length());
        verify(resenaRepository, times(1)).save(resena);
    }

    // ─── CP09: Borde — Crear reseña con calificación = 0 (fuera del rango) ──

    @Test
    void crearResena_lanzaExcepcion_cuandoCalificacionEsCero() {
        // Arrange
        resena.setCalificacion(0);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> resenaService.crearResena(resena));

        assertEquals("La calificación debe estar entre 1 y 5", ex.getMessage());
        verify(resenaRepository, never()).save(any());
    }

    // ─── CP10: Lógica de negocio — Crear reseña válida completa ─────────────

    @Test
    void crearResena_guardaYRetorna_cuandoDatosCompletos() {
        // Arrange
        when(resenaRepository.save(any(Resena.class))).thenReturn(resena);

        // Act
        Resena resultado = resenaService.crearResena(resena);

        // Assert
        assertNotNull(resultado);
        assertEquals("Excelente materia, muy buen contenido", resultado.getComentario());
        assertEquals(5, resultado.getCalificacion());
        assertEquals("MATERIA", resultado.getTipo());
        assertEquals("Fundamentos de Ingeniería de Software", resultado.getObjetivo());
        verify(resenaRepository, times(1)).save(resena);
    }
}
