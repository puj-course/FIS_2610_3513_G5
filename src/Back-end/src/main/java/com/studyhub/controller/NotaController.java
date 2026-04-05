package com.studyhub.controller;

import com.studyhub.model.Nota;
import com.studyhub.service.NotaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notas")
@CrossOrigin(origins = "*")
public class NotaController {

    private final NotaService notaService;

    public NotaController(NotaService notaService) {
        this.notaService = notaService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearNota(@RequestBody Nota nota) {
        if (nota.getNombre() == null || nota.getNombre().trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("mensaje", "El nombre de la nota es obligatorio");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        if (nota.getCalificacion() == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("mensaje", "La calificación es obligatoria");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        if (nota.getPorcentaje() == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("mensaje", "El porcentaje es obligatorio");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        Nota notaGuardada = notaService.agregarNota(nota);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Nota guardada exitosamente");
        respuesta.put("nota", notaGuardada);
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Nota>> listarNotas() {
        return new ResponseEntity<>(notaService.obtenerTodasLasNotas(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerNotaPorId(@PathVariable Long id) {
        return notaService.obtenerNotaPorId(id)
                .map(nota -> new ResponseEntity<Object>(nota, HttpStatus.OK))
                .orElseGet(() -> {
                    Map<String, String> error = new HashMap<>();
                    error.put("mensaje", "Nota no encontrada");
                    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
                });
    }

    @GetMapping("/asignatura/{asignaturaId}")
    public ResponseEntity<List<Nota>> obtenerNotasPorAsignatura(@PathVariable Long asignaturaId) {
        return new ResponseEntity<>(notaService.obtenerNotasPorAsignatura(asignaturaId), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarNota(@PathVariable Long id,
                                                              @RequestBody Nota datosActualizados) {
        if (datosActualizados.getNombre() == null || datosActualizados.getNombre().trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("mensaje", "El nombre de la nota es obligatorio");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        return notaService.actualizarNota(id, datosActualizados)
                .map(notaActualizada -> {
                    Map<String, Object> respuesta = new HashMap<>();
                    respuesta.put("mensaje", "Nota actualizada exitosamente");
                    respuesta.put("nota", notaActualizada);
                    return new ResponseEntity<>(respuesta, HttpStatus.OK);
                })
                .orElseGet(() -> {
                    Map<String, Object> error = new HashMap<>();
                    error.put("mensaje", "Nota no encontrada");
                    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarNota(@PathVariable Long id) {
        Map<String, String> respuesta = new HashMap<>();
        if (notaService.eliminarNota(id)) {
            respuesta.put("mensaje", "Nota eliminada exitosamente");
            return new ResponseEntity<>(respuesta, HttpStatus.OK);
        } else {
            respuesta.put("mensaje", "Nota no encontrada");
            return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/promedio/{asignaturaId}")
    public ResponseEntity<Map<String, Object>> obtenerPromedio(@PathVariable Long asignaturaId) {

        double promedio = notaService.calcularPromedio(asignaturaId);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("promedio", promedio);

        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }
}
