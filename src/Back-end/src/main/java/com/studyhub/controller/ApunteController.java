package com.studyhub.controller;

import com.studyhub.model.Apunte;
import com.studyhub.service.ApunteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apuntes")
@CrossOrigin(origins = "*") // Para permitir conexiones desde el front-end localmente
public class ApunteController {

    @Autowired
    private ApunteService apunteService;

    @PostMapping
    public ResponseEntity<Apunte> crearApunte(@Valid @RequestBody Apunte apunte) {
        Apunte creado = apunteService.crearApunte(apunte);
        return ResponseEntity.ok(creado);
    }

    @GetMapping
    public ResponseEntity<List<Apunte>> listarApuntes() {
        return ResponseEntity.ok(apunteService.listarApuntes());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Apunte> actualizarApunte(@PathVariable Long id, @Valid @RequestBody Apunte apunte) {
        try {
            Apunte actualizado = apunteService.actualizarApunte(id, apunte);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarApunte(@PathVariable Long id) {
        try {
            apunteService.eliminarApunte(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
