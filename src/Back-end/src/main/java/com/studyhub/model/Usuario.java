package com.studyhub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios", uniqueConstraints = {
        @UniqueConstraint(columnNames = "correo")
})
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @Email
    @NotBlank
    private String correo;

    @NotBlank
    private String password;

    @NotBlank
    private String rol;

    /** Carrera universitaria del estudiante (editable desde el perfil) */
    private String carrera;

    /** Semestre actual del estudiante (editable desde el perfil) */
    private Integer semestre;

    /**
     * URL pública de la foto de perfil.
     * Apunta al endpoint /uploads/fotos-perfil/{nombreArchivo}
     * o null si el usuario no ha subido foto.
     */
    private String fotoPerfil;

    private LocalDateTime fechaRegistro;

    @PrePersist
    public void prePersist() {
        this.fechaRegistro = LocalDateTime.now();
    }

    // ===== GETTERS =====

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getCorreo() { return correo; }
    public String getPassword() { return password; }
    public String getRol() { return rol; }
    public String getCarrera() { return carrera; }
    public Integer getSemestre() { return semestre; }
    public String getFotoPerfil() { return fotoPerfil; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }

    // ===== SETTERS =====

    public void setId(Long id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setPassword(String password) { this.password = password; }
    public void setRol(String rol) { this.rol = rol; }
    public void setCarrera(String carrera) { this.carrera = carrera; }
    public void setSemestre(Integer semestre) { this.semestre = semestre; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    // Campos para recuperación de contraseña
    private String tokenRecuperacion;
    private LocalDateTime tokenExpiracion;

    public String getTokenRecuperacion() { return tokenRecuperacion; }
    public void setTokenRecuperacion(String tokenRecuperacion) { this.tokenRecuperacion = tokenRecuperacion; }
    public LocalDateTime getTokenExpiracion() { return tokenExpiracion; }
    public void setTokenExpiracion(LocalDateTime tokenExpiracion) { this.tokenExpiracion = tokenExpiracion; }
}