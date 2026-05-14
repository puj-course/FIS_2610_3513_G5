package com.studyhub.service;

import com.studyhub.dto.QualityMetricDTO;
import com.studyhub.dto.QualityReportDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TestResultLogger.class)
class MetricsServiceTest {

    private MetricsService metricsService;

    @BeforeEach
    void setUp() {
        metricsService = new MetricsService();
    }

    @Test
    void generateQualityReport_retornaReporteCompletoYEnExcelenteEstado() {
        // Act
        QualityReportDTO report = metricsService.generateQualityReport();

        // Assert
        assertNotNull(report, "El reporte generado no debe ser nulo");
        assertEquals("EXCELENTE", report.getOverallStatus(), "El estado global del sistema debe ser EXCELENTE");
        assertNotNull(report.getGeneratedAt(), "La fecha de generación debe estar presente");
        assertNotNull(report.getSummaryInterpretation(), "Debe existir un resumen interpretativo");

        // Validar métricas nativas calculadas
        List<QualityMetricDTO> natives = report.getNativeMetrics();
        assertNotNull(natives, "La lista de métricas nativas no debe ser nula");
        assertEquals(3, natives.size(), "Debe contener exactamente 3 métricas nativas");

        // Verificar existencia de cada métrica nativa
        assertTrue(natives.stream().anyMatch(m -> m.getName().contains("Complejidad Ciclomática")), "Debe incluir Complejidad Ciclomática");
        assertTrue(natives.stream().anyMatch(m -> m.getName().contains("Densidad de Comentarios")), "Debe incluir Densidad de Comentarios");
        assertTrue(natives.stream().anyMatch(m -> m.getName().contains("Cobertura de Pruebas")), "Debe incluir Cobertura de Pruebas");

        // Validar métricas de SonarQube integradas
        List<QualityMetricDTO> sonarMetrics = report.getSonarqubeMetrics();
        assertNotNull(sonarMetrics, "La lista de métricas de SonarQube no debe ser nula");
        assertEquals(2, sonarMetrics.size(), "Debe contener exactamente 2 métricas de SonarQube");

        assertTrue(sonarMetrics.stream().anyMatch(m -> m.getName().contains("Duplicidad de Código")), "Debe incluir Duplicidad de Código");
        assertTrue(sonarMetrics.stream().anyMatch(m -> m.getName().contains("Seguridad")), "Debe incluir Seguridad y Vulnerabilidades");
    }

    @Test
    void generateQualityReport_validaFormatoYValoresDeMetricasNativas() {
        // Act
        QualityReportDTO report = metricsService.generateQualityReport();
        List<QualityMetricDTO> natives = report.getNativeMetrics();

        // Assert para Complejidad Ciclomática
        QualityMetricDTO complejidad = natives.stream()
                .filter(m -> m.getName().contains("Complejidad Ciclomática"))
                .findFirst()
                .orElseThrow();
        assertNotNull(complejidad.getValue(), "El valor de complejidad no debe ser nulo");
        assertEquals("CODIGO_NATIVO", complejidad.getSource(), "La fuente debe ser CODIGO_NATIVO");

        // Assert para Cobertura
        QualityMetricDTO cobertura = natives.stream()
                .filter(m -> m.getName().contains("Cobertura de Pruebas"))
                .findFirst()
                .orElseThrow();
        assertTrue(cobertura.getValue().contains("%"), "El valor de cobertura debe ser un porcentaje");
        assertEquals("EXCELENTE", cobertura.getStatus(), "El estado de cobertura debe ser EXCELENTE");
    }
}
