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

    private void enviarTwilioSms(String to, String mensaje) {
        if (accountSid == null || accountSid.trim().isEmpty() || authToken == null || authToken.trim().isEmpty()) {
            System.out.println("⚠️ Credenciales de Twilio no configuradas en el entorno (TWILIO_ACCOUNT_SID / TWILIO_AUTH_TOKEN). Omitiendo envío físico de SMS a " + to);
            return;
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
                System.out.println("🚀 [SMS ENVIADO REAL] Twilio entregó el SMS exitosamente a " + to);
            } else {
                System.err.println("❌ [SMS FALLIDO REAL] Error de Twilio (" + response.statusCode() + "): " + response.body());
            }
        } catch (Exception e) {
            System.err.println("❌ [SMS ERROR DE RED] No se pudo conectar con Twilio para el SMS: " + e.getMessage());
        }
    }

    public void enviarAlertaLogin(String telefono, String nombreUsuario, String loginAt) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return;
        }
        String mensaje = "⚡ [SMS ALERTA LOGIN] Hola " + nombreUsuario + ", se ha iniciado sesión en tu cuenta de StudyHub el " + loginAt + ". Si no fuiste tú, por favor protege tu cuenta.";
        System.out.println("========================================================");
        System.out.println("⚡ [SMS ALERTA LOGIN] Preparando envío de mensaje de texto a: " + telefono);
        System.out.println(mensaje);
        System.out.println("========================================================");
        enviarTwilioSms(telefono, mensaje);
    }

    public void enviarSmsRecuperacion(String telefono, String enlace) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return;
        }
        String mensaje = "🔑 StudyHub - Para recuperar tu contraseña accede al siguiente enlace seguro: " + enlace;
        System.out.println("========================================================");
        System.out.println("🔑 [SMS RECUPERACIÓN] Preparando envío de mensaje de texto a: " + telefono);
        System.out.println(mensaje);
        System.out.println("========================================================");
        enviarTwilioSms(telefono, mensaje);
    }
}
