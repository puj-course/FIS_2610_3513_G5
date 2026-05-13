package com.studyhub.dto;

/**
 * DTO de respuesta para GET /api/usuarios/{id}/subjects.
 *
 * Representa una materia del usuario con los campos requeridos por la HU:
 * id, name, description, status, redirectUrl e iconUrl.
 *
 * status se deriva del progreso de calificaciones:
 *   - pendiente   → 0% evaluado
 *   - en_progreso → entre 1% y 99% evaluado
 *   - completada  → 100% evaluado
 */
public class SubjectSummaryDTO {

    private Long   id;
    private String name;
    private String description;
    private String status;
    private String redirectUrl;
    private String iconUrl;

    public SubjectSummaryDTO(Long id, String name, String description,
                             String status, String redirectUrl, String iconUrl) {
        this.id          = id;
        this.name        = name;
        this.description = description;
        this.status      = status;
        this.redirectUrl = redirectUrl;
        this.iconUrl     = iconUrl;
    }

    public Long   getId()          { return id; }
    public String getName()        { return name; }
    public String getDescription() { return description; }
    public String getStatus()      { return status; }
    public String getRedirectUrl() { return redirectUrl; }
    public String getIconUrl()     { return iconUrl; }

    public void setId(Long id)                  { this.id = id; }
    public void setName(String name)            { this.name = name; }
    public void setDescription(String desc)     { this.description = desc; }
    public void setStatus(String status)        { this.status = status; }
    public void setRedirectUrl(String url)      { this.redirectUrl = url; }
    public void setIconUrl(String iconUrl)      { this.iconUrl = iconUrl; }
}