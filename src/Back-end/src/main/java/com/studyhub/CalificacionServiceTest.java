package com.studyhub;

import com.studyhub.model.Calificacion;
import com.studyhub.repository.CalificacionRepository;
import com.studyhub.service.CalificacionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CalificacionServiceTest {

    @Mock
    private CalificacionRepository calificacionRepository;

    @InjectMocks
    private CalificacionService calificacionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void actualizarCalificacion_Exito() {
        Long id = 1L;
        Calificacion calificacionExistente = new Calificacion("Parcial 1", 3.0, 30.0, null);
        calificacionExistente.setId(id);
        
        Calificacion datosNuevos = new Calificacion("Parcial 1 Modificado", 4.5, 30.0, null);

        when(calificacionRepository.findById(id)).thenReturn(Optional.of(calificacionExistente));
        when(calificacionRepository.save(any(Calificacion.class))).thenReturn(calificacionExistente);

        Optional<Calificacion> resultado = calificacionService.actualizarCalificacion(id, datosNuevos);

        assertTrue(resultado.isPresent());
        assertEquals("Parcial 1 Modificado", resultado.get().getNombre());
        assertEquals(4.5, resultado.get().getCalificacion());
        verify(calificacionRepository, times(1)).save(any(Calificacion.class));
    }


    @Test
    void actualizarCalificacion_NoEncontrada() {
        Long id = 2L;
        Calificacion datosNuevos = new Calificacion("Test", 5.0, 10.0, null);

        when(calificacionRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Calificacion> resultado = calificacionService.actualizarCalificacion(id, datosNuevos);

        assertFalse(resultado.isPresent());
        verify(calificacionRepository, never()).save(any(Calificacion.class));
    }


    @Test
    void actualizarCalificacion_DatosInvalidos() {
        Long id = 1L;
        Calificacion calificacionExistente = new Calificacion("Original", 3.0, 20.0, null);
        calificacionExistente.setId(id);
        
        Calificacion datosNuevos = new Calificacion("", 5.0, 20.0, null); // Nombre vacío

        when(calificacionRepository.findById(id)).thenReturn(Optional.of(calificacionExistente));
        when(calificacionRepository.save(any(Calificacion.class))).thenReturn(calificacionExistente);

        Optional<Calificacion> resultado = calificacionService.actualizarCalificacion(id, datosNuevos);

        assertTrue(resultado.isPresent());
        assertEquals("Original", resultado.get().getNombre()); 
        assertEquals(5.0, resultado.get().getCalificacion());
        verify(calificacionRepository, times(1)).save(any(Calificacion.class));
    }
}
