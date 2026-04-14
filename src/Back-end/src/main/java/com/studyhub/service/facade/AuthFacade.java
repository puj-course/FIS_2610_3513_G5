package com.studyhub.service.facade;

import com.studyhub.dto.AuthResponse;
import com.studyhub.dto.LoginRequest;
import com.studyhub.dto.RegistroRequest;
import com.studyhub.model.Usuario;
import com.studyhub.service.UsuarioService;
import com.studyhub.model.UsuarioBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthFacade {

    @Autowired
    private UsuarioService usuarioService;

    public AuthResponse registrar(RegistroRequest request) {
        try {
            Usuario usuario = new UsuarioBuilder()
                    .nombre(request.getNombre())
                    .apellido(request.getApellido())
                    .correo(request.getCorreo())
                    .password(request.getPassword())
                    .rol(request.getRol() != null ? request.getRol() : "ESTUDIANTE")
                    .build();

            Usuario guardado = usuarioService.crearUsuario(usuario);
            return AuthResponse.ok("Usuario registrado exitosamente", guardado);

        } catch (IllegalArgumentException e) {
            return AuthResponse.error("Datos inválidos: " + e.getMessage());
        } catch (RuntimeException e) {
            return AuthResponse.error(e.getMessage());
        }
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Usuario usuario = usuarioService.login(request.getCorreo(), request.getPassword());
            return AuthResponse.ok("Inicio de sesión exitoso", usuario);
        } catch (RuntimeException e) {
            return AuthResponse.error(e.getMessage());
        }
    }
}
