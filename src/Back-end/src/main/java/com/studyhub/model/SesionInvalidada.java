package com.studyhub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sesiones_invalidadas")
public class SesionInvalidada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private LocalDateTime fechaLogout;

    public SesionInvalidada() {}

    public SesionInvalidada(Long usuarioId) {
        this.usuarioId   = usuarioId;
        this.fechaLogout = LocalDateTime.now();
    }

    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }
    public Long getUsuarioId()                  { return usuarioId; }
    public void setUsuarioId(Long u)            { this.usuarioId = u; }
    public LocalDateTime getFechaLogout()       { return fechaLogout; }
    public void setFechaLogout(LocalDateTime f) { this.fechaLogout = f; }
}


