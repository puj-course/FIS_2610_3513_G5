package com.studyhub.controller;

import com.studyhub.model.Asignatura;
import com.studyhub.repository.AsignaturaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/asignaturas")
@CrossOrigin(origins = "*")
public class AsignaturaController {

    private final AsignaturaRepository asignaturaRepository;

    public AsignaturaController(AsignaturaRepository asignaturaRepository) {
        this.asignaturaRepository = asignaturaRepository;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearAsignatura(@RequestBody Asignatura asignatura) {
        Asignatura asignaturaGuardada = asignaturaRepository.save(asignatura);
        System.out.println("Asignatura guardada en BD: " + asignaturaGuardada);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Asignatura guardada exitosamente");
        respuesta.put("asignatura", asignaturaGuardada);

        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Asignatura>> listarAsignaturas() {
        List<Asignatura> asignaturas = asignaturaRepository.findAll();
        return new ResponseEntity<>(asignaturas, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarAsignatura(@PathVariable Long id) {
        if (!asignaturaRepository.existsById(id)) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Asignatura no encontrada");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        asignaturaRepository.deleteById(id);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Asignatura eliminada exitosamente");
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }
}
