package com.studyhub.dto;

public class QualityMetricDTO {
    private String name;
    private String value;
    private String status; // "EXCELENTE", "BUENO", "ADVERTENCIA"
    private String interpretation;
    private String source; // "CODIGO_NATIVO", "SONARQUBE"

    public QualityMetricDTO() {}

    public QualityMetricDTO(String name, String value, String status, String interpretation, String source) {
        this.name = name;
        this.value = value;
        this.status = status;
        this.interpretation = interpretation;
        this.source = source;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getInterpretation() { return interpretation; }
    public void setInterpretation(String interpretation) { this.interpretation = interpretation; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
