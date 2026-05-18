package com.studyhub.service;

import org.springframework.stereotype.Service;

@Service
public class SmsService {

    public void enviarAlertaLogin(String telefono, String nombreUsuario, String loginAt) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return;
        }
        System.out.println("========================================================");
        System.out.println("⚡ [SMS ALERTA LOGIN] Enviando mensaje de texto a: " + telefono);
        System.out.println("Mensaje: Hola " + nombreUsuario + ", se ha iniciado sesión en tu cuenta de StudyHub el " + loginAt + ". Si no fuiste tú, por favor protege tu cuenta.");
        System.out.println("========================================================");
    }

    public void enviarSmsRecuperacion(String telefono, String enlace) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return;
        }
        System.out.println("========================================================");
        System.out.println("🔑 [SMS RECUPERACIÓN] Enviando mensaje de texto a: " + telefono);
        System.out.println("Mensaje: StudyHub - Para recuperar tu contraseña accede al siguiente enlace seguro: " + enlace);
        System.out.println("========================================================");
    }
}
