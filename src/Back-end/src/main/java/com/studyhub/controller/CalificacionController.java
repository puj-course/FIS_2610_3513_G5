package com.studyhub.controller;

import com.studyhub.model.Calificacion;
import com.studyhub.service.CalificacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/calificaciones")
@CrossOrigin(origins = "*")
public class CalificacionController {

    private final CalificacionService calificacionService;

    public CalificacionController(CalificacionService calificacionService) {
        this.calificacionService = calificacionService;
    }

    @PostMapping
    public ResponseEntity<Calificacion> crear(@RequestBody Calificacion calificacion) {
        return new ResponseEntity<>(calificacionService.agregarCalificacion(calificacion), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Calificacion>> listar() {
        return ResponseEntity.ok(calificacionService.obtenerTodas());
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Calificacion datos) {

        if (datos.getNombre() == null || datos.getNombre().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre de la calificación es obligatorio");
        }

        Optional<Calificacion> actualizada = calificacionService.actualizarCalificacion(id, datos);
        
        if (actualizada.isPresent()) {
            return ResponseEntity.ok(actualizada.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Calificación no encontrada");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (calificacionService.eliminarCalificacion(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
