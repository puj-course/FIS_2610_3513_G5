package com.studyhub.controller;

import com.studyhub.model.Tarea;
import com.studyhub.repository.TareaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tareas")
@CrossOrigin(origins = "*")
public class TareaController {

    private final TareaRepository tareaRepository;

    public TareaController(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearTarea(@RequestBody Tarea tarea) {
        tarea.setEstado(true); // toda tarea nueva nace como pendiente
        Tarea tareaGuardada = tareaRepository.save(tarea);
        System.out.println("Tarea guardada en BD: " + tareaGuardada);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Tarea guardada exitosamente");
        respuesta.put("tarea", tareaGuardada);

        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Tarea>> listarTareas() {
        List<Tarea> tareas = tareaRepository.findAll();
        return new ResponseEntity<>(tareas, HttpStatus.OK);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Map<String, Object>> toggleEstado(@PathVariable Long id) {
        return tareaRepository.findById(id)
                .map(tarea -> {
                    tarea.setEstado(!tarea.isEstado()); // invierte el estado actual
                    tareaRepository.save(tarea);

                    String mensaje = tarea.isEstado() ? "Tarea marcada como pendiente" : "Tarea marcada como terminada";

                    Map<String, Object> respuesta = new HashMap<>();
                    respuesta.put("mensaje", mensaje);
                    respuesta.put("tarea", tarea);

                    return new ResponseEntity<>(respuesta, HttpStatus.OK);
                })
                .orElseGet(() -> {
                    Map<String, Object> error = new HashMap<>();
                    error.put("mensaje", "Tarea no encontrada");
                    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarTarea(@PathVariable Long id) {
        if (!tareaRepository.existsById(id)) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Tarea no encontrada");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        tareaRepository.deleteById(id);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Tarea eliminada exitosamente");
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }
}