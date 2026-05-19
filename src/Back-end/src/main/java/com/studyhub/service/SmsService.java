package com.studyhub.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

@Service
public class SmsService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.verify.service.sid}")
    private String verifyServiceSid;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    // ─────────────────────────────────────────────────────────────────────────
    // MÉTODO PRINCIPAL: Envía el código OTP vía Twilio Verify
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Envía un código de verificación de 6 dígitos al número indicado
     * usando el servicio Twilio Verify.
     *
     * @param telefono Número en formato E.164 (ej: +573142733826)
     * @return true si Twilio aceptó el envío, false en caso contrario
     */
    public boolean enviarCodigoVerificacion(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            System.err.println("❌ [TWILIO VERIFY] Teléfono vacío, no se envía código.");
            return false;
        }

        System.out.println("========================================================");
        System.out.println("📲 [TWILIO VERIFY] Enviando código OTP a: " + telefono);
        System.out.println("========================================================");

        try {
            String url = "https://verify.twilio.com/v2/Services/" + verifyServiceSid + "/Verifications";

            String body = "To=" + java.net.URLEncoder.encode(telefono.trim(), "UTF-8")
                    + "&Channel=sms";

            String credenciales = accountSid + ":" + authToken;
            String basicAuth = "Basic " + Base64.getEncoder().encodeToString(credenciales.getBytes());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", basicAuth)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("🚀 [TWILIO VERIFY] Respuesta HTTP " + response.statusCode() + ": " + response.body());

            if (response.statusCode() == 201) {
                System.out.println("✅ [TWILIO VERIFY] Código OTP enviado exitosamente a " + telefono);
                return true;
            } else {
                System.err.println("⚠️ [TWILIO VERIFY] Error al enviar código: " + response.body());
                return false;
            }

        } catch (Exception e) {
            System.err.println("❌ [TWILIO VERIFY] Error de red: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MÉTODO: Verifica el código OTP ingresado por el usuario
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Verifica si el código OTP ingresado por el usuario es correcto.
     *
     * @param telefono Número en formato E.164 (ej: +573142733826)
     * @param codigo   Código de 6 dígitos ingresado por el usuario
     * @return true si el código es válido, false en caso contrario
     */
    public boolean verificarCodigo(String telefono, String codigo) {
        if (telefono == null || codigo == null) {
            return false;
        }

        System.out.println("========================================================");
        System.out.println("🔍 [TWILIO VERIFY] Verificando código para: " + telefono);
        System.out.println("========================================================");

        try {
            String url = "https://verify.twilio.com/v2/Services/" + verifyServiceSid + "/VerificationCheck";

            String body = "To=" + java.net.URLEncoder.encode(telefono.trim(), "UTF-8")
                    + "&Code=" + java.net.URLEncoder.encode(codigo.trim(), "UTF-8");

            String credenciales = accountSid + ":" + authToken;
            String basicAuth = "Basic " + Base64.getEncoder().encodeToString(credenciales.getBytes());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", basicAuth)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("🚀 [TWILIO VERIFY] Respuesta verificación HTTP " + response.statusCode() + ": " + response.body());

            if (response.statusCode() == 200 && response.body().contains("\"status\":\"approved\"")) {
                System.out.println("✅ [TWILIO VERIFY] Código correcto para " + telefono);
                return true;
            } else {
                System.err.println("⚠️ [TWILIO VERIFY] Código incorrecto o expirado para " + telefono);
                return false;
            }

        } catch (Exception e) {
            System.err.println("❌ [TWILIO VERIFY] Error de red al verificar: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MÉTODO LEGACY: Alerta de login (se mantiene igual)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Envía un código OTP como alerta de nuevo inicio de sesión.
     * Reutiliza el flujo de Twilio Verify para notificar al usuario.
     */
    public void enviarAlertaLogin(String telefono, String nombreUsuario, String loginAt) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return;
        }
        System.out.println("🔔 [TWILIO VERIFY] Enviando alerta de login a " + nombreUsuario + " (" + telefono + ") - " + loginAt);
        enviarCodigoVerificacion(telefono);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MÉTODO LEGACY: SMS de recuperación (ahora envía código OTP)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Envía un código OTP de 6 dígitos para recuperación de contraseña.
     * El código es generado y validado por Twilio Verify.
     *
     * @param telefono Número del usuario
     * @return true si el código fue enviado exitosamente
     */
    public boolean enviarSmsRecuperacion(String telefono) {
        return enviarCodigoVerificacion(telefono);
    }
}