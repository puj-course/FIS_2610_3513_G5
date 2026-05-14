package com.studyhub.controller;

import com.studyhub.model.Notification;
import com.studyhub.repository.NotificationRepository;
import com.studyhub.service.NotificationService;
import com.studyhub.service.TelegramService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.*;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationRepository notificationRepo;
    private final NotificationService    notificationService;
    private final TelegramService        telegramService;

    public NotificationController(NotificationRepository notificationRepo,
                                  NotificationService notificationService,
                                  TelegramService telegramService) {
        this.notificationRepo    = notificationRepo;
        this.notificationService = notificationService;
        this.telegramService     = telegramService;
    }

    /** GET /api/notifications?userId=1  — lista ordenada por más reciente */
    @GetMapping
    public ResponseEntity<List<Notification>> listar(@RequestParam Long userId) {
        return ResponseEntity.ok(
            notificationRepo.findByUserIdOrderByCreatedAtDesc(userId)
        );
    }

    /** PATCH /api/notifications/{id}/read  — marca como leída */
    @PatchMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> marcarLeida(@PathVariable Long id) {
        return notificationRepo.findById(id).map(n -> {
            n.setStatus("LEIDA");
            notificationRepo.save(n);
            Map<String, Object> r = new HashMap<>();
            r.put("mensaje", "Notificación marcada como leída");
            r.put("notification", n);
            return ResponseEntity.ok(r);
        }).orElse(ResponseEntity.notFound().build());
    }

    /** DELETE /api/notifications/{id}  — descarta la notificación */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        if (!notificationRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        notificationRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("mensaje", "Notificación eliminada"));
    }

    /** GET /api/notifications/stream?userId=1  — canal SSE en tiempo real */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestParam Long userId) {
        return notificationService.suscribir(userId);
    }

    /** POST /api/notifications/telegram — Envia notificacion push instantanea a Telegram */
    @PostMapping("/telegram")
    public ResponseEntity<Map<String, Object>> sendTelegramPush(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        String chatId = payload.get("chatId"); // opcional

        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "mensaje", "El mensaje es obligatorio"));
        }

        boolean enviado = telegramService.sendTelegramNotification(message, chatId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", enviado);
        response.put("mensaje", enviado ? "Notificación enviada a Telegram exitosamente" : "Error al notificar por Telegram");

        return ResponseEntity.ok(response);
    }
}
