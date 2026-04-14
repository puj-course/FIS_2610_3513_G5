package com.studyhub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "asignaturas")
public class Asignatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String codigo;
    private String profesor;
    private String horario;
    private int creditos;
    private String periodo;
    
    @jakarta.persistence.Transient
    private double progreso;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Asignatura() {
    }

    public Asignatura(String nombre, String codigo, String profesor,
                      String horario, int creditos, String periodo, Usuario usuario) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.profesor = profesor;
        this.horario = horario;
        this.creditos = creditos;
        this.periodo = periodo;
        this.usuario = usuario;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getProfesor() { return profesor; }
    public void setProfesor(String profesor) { this.profesor = profesor; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public int getCreditos() { return creditos; }
    public void setCreditos(int creditos) { this.creditos = creditos; }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public double getProgreso() { return progreso; }
    public void setProgreso(double progreso) { this.progreso = progreso; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    @Override
    public String toString() {
        return "Asignatura{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", codigo='" + codigo + '\'' +
                ", profesor='" + profesor + '\'' +
                ", horario='" + horario + '\'' +
                ", creditos=" + creditos +
                ", periodo='" + periodo + '\'' +
                ", usuario=" + (usuario != null ? usuario.getId() : "null") +
                '}';
    }
}