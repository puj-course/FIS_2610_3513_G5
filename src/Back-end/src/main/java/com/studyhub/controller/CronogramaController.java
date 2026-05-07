package com.studyhub.controller;

import com.studyhub.model.Asignacion;
import com.studyhub.service.CronogramaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cronogramas")
@CrossOrigin(origins = "*")
public class CronogramaController {

    private final CronogramaService cronogramaService;

    public CronogramaController(CronogramaService cronogramaService) {
        this.cronogramaService = cronogramaService;
    }

    // GET /api/cronogramas?fecha=2026-05-10 (Sub-issue #477)
    @GetMapping
    public ResponseEntity<List<Asignacion>> listar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(cronogramaService.obtenerAsignacionesPorSemana(fecha));
    }

    // PUT /api/cronogramas/asignaciones/{id} (Sub-issue #477 / #479)
    @PutMapping("/asignaciones/{id}")
    public ResponseEntity<Asignacion> actualizar(@PathVariable Long id, @RequestBody Asignacion asignacion) {
        asignacion.setId(id);
        return ResponseEntity.ok(cronogramaService.guardarAsignacion(asignacion));
    }
}
