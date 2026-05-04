package com.studyhub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID del usuario dueño de la notificación */
    @Column(nullable = false)
    private Long userId;

    /** Tipo de evento: TAREA, MENSAJE, CALIFICACION, SISTEMA */
    @Column(nullable = false)
    private String type;

    /** Texto visible al usuario */
    @Column(nullable = false)
    private String message;

    /** Estado: NO_LEIDA, LEIDA, ARCHIVADA */
    @Column(nullable = false)
    private String status = "NO_LEIDA";

    /** Prioridad: NORMAL, CRITICA */
    @Column(nullable = false)
    private String priority = "NORMAL";

    /** URL a la que redirige el botón de acción (puede ser null) */
    @Column
    private String actionUrl;

    /** Metadatos adicionales en formato JSON (opcional) */
    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null)  this.status   = "NO_LEIDA";
        if (this.priority == null) this.priority = "NORMAL";
    }

    // ── Getters y Setters ──────────────────────────────────────
    public Long getId()             { return id; }
    public Long getUserId()         { return userId; }
    public void setUserId(Long v)   { this.userId = v; }
    public String getType()         { return type; }
    public void setType(String v)   { this.type = v; }
    public String getMessage()      { return message; }
    public void setMessage(String v){ this.message = v; }
    public String getStatus()       { return status; }
    public void setStatus(String v) { this.status = v; }
    public String getPriority()     { return priority; }
    public void setPriority(String v){ this.priority = v; }
    public String getActionUrl()    { return actionUrl; }
    public void setActionUrl(String v){ this.actionUrl = v; }
    public String getMetadata()     { return metadata; }
    public void setMetadata(String v){ this.metadata = v; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
}
