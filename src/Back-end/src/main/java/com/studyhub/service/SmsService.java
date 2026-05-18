package com.studyhub.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class SmsService {

    @Value("${twilio.account-sid:}")
    private String accountSid;

    @Value("${twilio.auth-token:}")
    private String authToken;

    @Value("${twilio.phone-number:}")
    private String fromPhoneNumber;

    private final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

    private boolean enviarTwilioSms(String to, String mensaje) {
        if (accountSid == null || accountSid.trim().isEmpty() || authToken == null || authToken.trim().isEmpty()) {
            return false;
        }
        try {
            String url = "https://api.twilio.com/2010-04-01/Accounts/" + accountSid.trim() + "/Messages.json";
            String from = (fromPhoneNumber != null && !fromPhoneNumber.trim().isEmpty()) ? fromPhoneNumber.trim() : "+1234567890";
            
            String formData = "To=" + URLEncoder.encode(to.trim(), StandardCharsets.UTF_8) +
                              "&From=" + URLEncoder.encode(from, StandardCharsets.UTF_8) +
                              "&Body=" + URLEncoder.encode(mensaje, StandardCharsets.UTF_8);

            String authHeader = "Basic " + Base64.getEncoder().encodeToString((accountSid.trim() + ":" + authToken.trim()).getBytes(StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", authHeader)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("🚀 [SMS TWILIO] Entregado exitosamente a " + to);
                return true;
            } else {
                System.err.println("❌ [SMS TWILIO ERROR]: " + response.body());
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ [SMS TWILIO RED]: " + e.getMessage());
            return false;
        }
    }

    private boolean enviarTextbeltSms(String to, String mensaje) {
        try {
            String msgEscapado = mensaje.replace("\"", "\\\"");
            String jsonPayload = "{\"phone\": \"" + to.trim() + "\", \"message\": \"" + msgEscapado + "\", \"key\": \"textbelt\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://textbelt.com/text"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("🚀 [SMS TEXTBELT] Respuesta de pasarela: " + response.body());
            return response.body().contains("\"success\":true") || response.body().contains("\"success\": true");
        } catch (Exception e) {
            System.err.println("❌ [SMS TEXTBELT ERROR]: " + e.getMessage());
            return false;
        }
    }

    private void enviarSmsFisico(String to, String mensaje) {
        System.out.println("========================================================");
        System.out.println("📲 [SMS EN CURSO] Destinatario: " + to);
        System.out.println("Contenido: " + mensaje);
        System.out.println("========================================================");

        // 1. Intentar por Twilio si están configuradas las credenciales
        boolean exito = enviarTwilioSms(to, mensaje);
        if (exito) {
            return;
        }

        // 2. Fallback automático a Textbelt API Gratuita
        System.out.println("⚠️ Intentando entrega por pasarela de respaldo (Textbelt Free API)...");
        enviarTextbeltSms(to, mensaje);
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
