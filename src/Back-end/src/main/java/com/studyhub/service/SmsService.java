package com.studyhub.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class SmsService {

    @Value("${sms.gateway.key:textbelt}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

    private void enviarSmsFisico(String to, String mensaje) {
        System.out.println("========================================================");
        System.out.println("📲 [SMS EN CURSO] Destinatario: " + to);
        System.out.println("Contenido: " + mensaje);
        System.out.println("========================================================");

        try {
            // Limpiar y escapar comillas para JSON limpio
            String msgEscapado = mensaje.replace("\"", "\\\"");
            String jsonPayload = "{\"phone\": \"" + to.trim() + "\", \"message\": \"" + msgEscapado + "\", \"key\": \"" + apiKey.trim() + "\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://textbelt.com/text"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("🚀 [SMS TEXTBELT] Respuesta de pasarela: " + response.body());
            
            if (response.body().contains("\"success\":true") || response.body().contains("\"success\": true")) {
                System.out.println("✅ Mensaje entregado exitosamente a la red celular de " + to);
            } else {
                System.err.println("⚠️ Nota de pasarela: " + response.body());
            }
        } catch (Exception e) {
            System.err.println("❌ [SMS ERROR DE RED]: " + e.getMessage());
        }
    }

    public void enviarAlertaLogin(String telefono, String nombreUsuario, String loginAt) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return;
        }
        String mensaje = "⚡ [StudyHub] Hola " + nombreUsuario + ", nuevo inicio de sesion detectado el " + loginAt + ". Protege tu cuenta.";
        enviarSmsFisico(telefono, mensaje);
    }

    public void enviarSmsRecuperacion(String telefono, String enlace) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return;
        }
        String mensaje = "🔑 [StudyHub] Accede aqui para recuperar tu contrasena: " + enlace;
        enviarSmsFisico(telefono, mensaje);
    }
}
