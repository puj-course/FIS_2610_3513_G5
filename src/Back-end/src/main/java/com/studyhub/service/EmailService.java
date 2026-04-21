package com.studyhub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void enviarCorreoRecuperacion(String destinatario, String enlace) {
        if (mailSender == null) {
            System.out.println("DEBUG: JavaMailSender no está configurado. Simulación de envío a: " + destinatario);
            System.out.println("DEBUG: Enlace de recuperación: " + enlace);
            return;
        }

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("Recuperación de contraseña - StudyHub");
        mensaje.setText("Hola,\n\nPara restablecer tu contraseña, haz clic en el siguiente enlace:\n" 
                + enlace + "\n\nSi no solicitaste este cambio, puedes ignorar este correo.");
        
        try {
            mailSender.send(mensaje);
        } catch (Exception e) {
            System.err.println("Error al enviar correo: " + e.getMessage());
            // En entorno de desarrollo, seguimos imprimiendo el enlace para facilitar las pruebas
            System.out.println("DEBUG: Enlace de recuperación (fallo envío): " + enlace);
        }
    }
}
