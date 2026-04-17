package com.studyhub.controller;

import com.studyhub.model.Resena;
import com.studyhub.service.ResenaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/resenas")
@CrossOrigin(origins = "*")
public class ResenaController {

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @PostMapping
    public ResponseEntity<?> crearResena(@RequestBody Resena resena) {
        try {
            if (resena.getUsuario() == null || resena.getUsuario().getId() == null) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "El usuario es obligatorio"));
            }
            Resena nueva = resenaService.crearResena(resena);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<Page<Resena>> obtenerResenas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());
        return ResponseEntity.ok(resenaService.obtenerResenas(pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarResena(@PathVariable Long id, @RequestParam Long usuarioId) {
        try {
            resenaService.eliminarResena(id, usuarioId);
            return ResponseEntity.ok(Map.of("mensaje", "Reseña eliminada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", e.getMessage()));
        }
    }
}
