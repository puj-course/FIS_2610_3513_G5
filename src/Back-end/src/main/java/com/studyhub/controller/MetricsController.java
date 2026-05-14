package com.studyhub.controller;

import com.studyhub.dto.QualityReportDTO;
import com.studyhub.service.MetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
@CrossOrigin(origins = "*")
public class MetricsController {

    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/quality")
    public ResponseEntity<QualityReportDTO> getQualityReport() {
        return ResponseEntity.ok(metricsService.generateQualityReport());
    }
}
