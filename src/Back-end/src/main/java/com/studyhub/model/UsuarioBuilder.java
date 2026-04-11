package com.studyhub.model;

public class UsuarioBuilder {

    private String nombre;
    private String apellido;
    private String correo;
    private String password;
    private String rol = "ESTUDIANTE";

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

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setCorreo(correo);
        usuario.setPassword(password);
        usuario.setRol(rol);
        return usuario;
    }
}
