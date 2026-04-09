package com.studyhub.dto;

public class AuthResponse {

    private boolean exito;
    private String mensaje;
    private Object datos;

    public AuthResponse(boolean exito, String mensaje, Object datos) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.datos = datos;
    }

    public static AuthResponse ok(String mensaje, Object datos) {
        return new AuthResponse(true, mensaje, datos);
    }

    public static AuthResponse error(String mensaje) {
        return new AuthResponse(false, mensaje, null);
    }

    public boolean isExito() { return exito; }
    public void setExito(boolean exito) { this.exito = exito; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Object getDatos() { return datos; }
    public void setDatos(Object datos) { this.datos = datos; }
}
