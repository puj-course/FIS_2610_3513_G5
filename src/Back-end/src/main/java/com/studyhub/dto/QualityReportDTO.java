package com.studyhub.dto;

import java.util.List;

public class QualityReportDTO {
    private String generatedAt;
    private String overallStatus;
    private List<QualityMetricDTO> nativeMetrics;
    private List<QualityMetricDTO> sonarqubeMetrics;
    private String summaryInterpretation;

    public QualityReportDTO() {}

    public QualityReportDTO(String generatedAt, String overallStatus, List<QualityMetricDTO> nativeMetrics, List<QualityMetricDTO> sonarqubeMetrics, String summaryInterpretation) {
        this.generatedAt = generatedAt;
        this.overallStatus = overallStatus;
        this.nativeMetrics = nativeMetrics;
        this.sonarqubeMetrics = sonarqubeMetrics;
        this.summaryInterpretation = summaryInterpretation;
    }

    public String getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }

    public String getOverallStatus() { return overallStatus; }
    public void setOverallStatus(String overallStatus) { this.overallStatus = overallStatus; }

    public List<QualityMetricDTO> getNativeMetrics() { return nativeMetrics; }
    public void setNativeMetrics(List<QualityMetricDTO> nativeMetrics) { this.nativeMetrics = nativeMetrics; }

    public List<QualityMetricDTO> getSonarqubeMetrics() { return sonarqubeMetrics; }
    public void setSonarqubeMetrics(List<QualityMetricDTO> sonarqubeMetrics) { this.sonarqubeMetrics = sonarqubeMetrics; }

    public String getSummaryInterpretation() { return summaryInterpretation; }
    public void setSummaryInterpretation(String summaryInterpretation) { this.summaryInterpretation = summaryInterpretation; }
}
