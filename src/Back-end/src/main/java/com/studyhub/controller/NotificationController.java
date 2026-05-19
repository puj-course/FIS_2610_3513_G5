package com.studyhub.controller;

import com.studyhub.model.Notification;
import com.studyhub.repository.NotificationRepository;
import com.studyhub.service.NotificationService;

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

    public NotificationController(NotificationRepository notificationRepo,
                                  NotificationService notificationService) {
        this.notificationRepo    = notificationRepo;
        this.notificationService = notificationService;
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
}