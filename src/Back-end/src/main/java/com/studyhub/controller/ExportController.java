package com.studyhub.controller;

import com.studyhub.model.Asignacion;
import com.studyhub.service.CronogramaService;
import com.studyhub.service.ExportService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cronogramas/export")
@CrossOrigin(origins = "*")
public class ExportController {

    private final CronogramaService cronogramaService;
    private final ExportService exportService;

    public ExportController(CronogramaService cronogramaService, ExportService exportService) {
        this.cronogramaService = cronogramaService;
        this.exportService = exportService;
    }

    @GetMapping
    public ResponseEntity<Object> exportar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam String formato) {

        List<Asignacion> asignaciones = cronogramaService.obtenerAsignacionesPorSemana(fecha);

        if ("pdf".equalsIgnoreCase(formato)) {
            ByteArrayInputStream bis = exportService.generarPdf(asignaciones);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=cronograma.pdf");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(bis));
        } else {
            String csv = exportService.generarCsv(asignaciones);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cronograma.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(csv);
        }
    }
}
