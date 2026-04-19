package com.studyhub.model;

/**
 * Builder para construir instancias de Usuario de forma fluida.
 * Los campos nombre, apellido, correo y password son obligatorios.
 * Los campos carrera y semestre son opcionales.
 */
public class UsuarioBuilder {

    private String nombre;
    private String apellido;
    private String correo;
    private String password;
    private String rol      = "ESTUDIANTE";
    private String carrera;
    private Integer semestre;

    public UsuarioBuilder nombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public UsuarioBuilder apellido(String apellido) {
        this.apellido = apellido;
        return this;
    }

    public UsuarioBuilder correo(String correo) {
        this.correo = correo;
        return this;
    }

    public UsuarioBuilder password(String password) {
        this.password = password;
        return this;
    }

    public UsuarioBuilder rol(String rol) {
        this.rol = rol;
        return this;
    }

    /** Carrera universitaria del estudiante (opcional). */
    public UsuarioBuilder carrera(String carrera) {
        this.carrera = carrera;
        return this;
    }

    /** Semestre actual del estudiante, entre 1 y 12 (opcional). */
    public UsuarioBuilder semestre(Integer semestre) {
        this.semestre = semestre;
        return this;
    }

    public Usuario build() {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }
        if (correo == null || correo.isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        if (semestre != null && (semestre < 1 || semestre > 12)) {
            throw new IllegalArgumentException("El semestre debe estar entre 1 y 12");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setCorreo(correo);
        usuario.setPassword(password);
        usuario.setRol(rol);
        usuario.setCarrera(carrera);
        usuario.setSemestre(semestre);
        return usuario;
    }
}