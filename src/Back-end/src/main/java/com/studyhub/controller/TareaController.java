package com.studyhub.controller;

import com.studyhub.model.Asignatura;
import com.studyhub.model.Tarea;
import com.studyhub.repository.AsignaturaRepository;
import com.studyhub.repository.TareaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// CORREGIDO: se agrega el prefijo /api para ser consistente con los demás controladores
@RestController
@RequestMapping("/api/tareas")
@CrossOrigin(origins = "*")
public class TareaController {

    private final TareaRepository tareaRepository;
    private final AsignaturaRepository asignaturaRepository;

    public TareaController(TareaRepository tareaRepository, AsignaturaRepository asignaturaRepository) {
        this.tareaRepository = tareaRepository;
        this.asignaturaRepository = asignaturaRepository;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearTarea(@RequestBody Map<String, Object> body) {
        Map<String, Object> respuesta = new HashMap<>();

        Long asignaturaId = Long.valueOf(body.get("asignaturaId").toString());
        Asignatura asignatura = asignaturaRepository.findById(asignaturaId).orElse(null);
        if (asignatura == null) {
            respuesta.put("mensaje", "Asignatura no encontrada");
            return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
        }

        Tarea tarea = new Tarea();
        tarea.setTitulo(body.get("titulo").toString());
        tarea.setAsignatura(asignatura);
        try {
            tarea.setFechaEntrega(LocalDate.parse(body.get("fechaEntrega").toString()));
            tarea.setHoraEntrega(LocalTime.parse(body.get("horaEntrega").toString()));
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("mensaje", "Formato de fecha u hora inválido");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        tarea.setDescripcion(body.get("descripcion") != null ? body.get("descripcion").toString() : null);
        tarea.setEstado(true);

        Tarea tareaGuardada = tareaRepository.save(tarea);
        respuesta.put("mensaje", "Tarea guardada exitosamente");
        respuesta.put("tarea", tareaGuardada);
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Tarea>> listarTareas(@RequestParam(required = false) Long usuarioId) {
        List<Tarea> tareas = usuarioId != null
                ? tareaRepository.findByAsignatura_Usuario_Id(usuarioId)
                : tareaRepository.findAll();
        return new ResponseEntity<>(tareas, HttpStatus.OK);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Map<String, Object>> toggleEstado(@PathVariable Long id) {
        return tareaRepository.findById(id)
                .map(tarea -> {
                    tarea.setEstado(!tarea.isEstado());
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

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<Tarea>> obtenerTareasPorFecha(@PathVariable String fecha) {
        try {
            LocalDate fechaParsed = LocalDate.parse(fecha);
            return new ResponseEntity<>(tareaRepository.findByFechaEntrega(fechaParsed), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
