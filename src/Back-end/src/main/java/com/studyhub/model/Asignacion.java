package com.studyhub.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "asignaciones")
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "turno_id")
    private Turno turno;

    @ManyToOne
    @JoinColumn(name = "cronograma_id")
    private Cronograma cronograma;

    private String proyecto;
    private LocalDate fecha;
    private Integer horasDiarias;
    private Boolean tieneConflicto = false;
    private String motivoAusencia;

    public Asignacion() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Turno getTurno() { return turno; }
    public void setTurno(Turno turno) { this.turno = turno; }
    public Cronograma getCronograma() { return cronograma; }
    public void setCronograma(Cronograma cronograma) { this.cronograma = cronograma; }
    public String getProyecto() { return proyecto; }
    public void setProyecto(String proyecto) { this.proyecto = proyecto; }
    public LocalDate getFecha() { return fecha; }
    public void setDate(LocalDate fecha) { this.fecha = fecha; }
    public Integer getHorasDiarias() { return horasDiarias; }
    public void setHorasDiarias(Integer horasDiarias) { this.horasDiarias = horasDiarias; }
    public Boolean getTieneConflicto() { return tieneConflicto; }
    public void setTieneConflicto(Boolean tieneConflicto) { this.tieneConflicto = tieneConflicto; }
    public String getMotivoAusencia() { return motivoAusencia; }
    public void setMotivoAusencia(String motivoAusencia) { this.motivoAusencia = motivoAusencia; }
}
