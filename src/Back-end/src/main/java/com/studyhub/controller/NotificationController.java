package com.studyhub.controller;

import com.studyhub.model.Notification;
import com.studyhub.repository.NotificationRepository;
import com.studyhub.service.NotificationService;
import com.studyhub.service.command.EliminarNotificacionCommand;
import com.studyhub.service.command.MarcarLeidaCommand;
import com.studyhub.service.command.NotificacionCommand;
import com.studyhub.service.command.NotificacionCommandInvoker;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationRepository     notificationRepo;
    private final NotificationService        notificationService;
    private final NotificacionCommandInvoker invoker;

    public NotificationController(NotificationRepository notificationRepo,
                                  NotificationService notificationService,
                                  NotificacionCommandInvoker invoker) {
        this.notificationRepo = notificationRepo;
        this.notificationService = notificationService;
        this.invoker = invoker;
    }

    // ── GET /api/notifications?userId=1 ──────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<Notification>> listar(@RequestParam Long userId) {
        return ResponseEntity.ok(
            notificationRepo.findByUserIdOrderByCreatedAtDesc(userId)
        );
    }

    // ── PATCH /api/notifications/{id}/read ───────────────────────────────────

    @PatchMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> marcarLeida(
            @PathVariable Long id,
            @RequestParam Long userId) {

        if (!notificationRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        MarcarLeidaCommand cmd = new MarcarLeidaCommand(notificationRepo, id);
        invoker.ejecutar(userId, cmd);

        Map<String, Object> resp = new HashMap<>();
        resp.put("mensaje",     cmd.getDescripcion());
        resp.put("undoDisponible", true);
        notificationRepo.findById(id).ifPresent(n -> resp.put("notification", n));
        return ResponseEntity.ok(resp);
    }

    // ── DELETE /api/notifications/{id} ───────────────────────────────────────

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(
            @PathVariable Long id,
            @RequestParam Long userId) {

        if (!notificationRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        EliminarNotificacionCommand cmd = new EliminarNotificacionCommand(notificationRepo, id);
        invoker.ejecutar(userId, cmd);

        Map<String, Object> resp = new HashMap<>();
        resp.put("mensaje",        cmd.getDescripcion());
        resp.put("undoDisponible", true);
        return ResponseEntity.ok(resp);
    }

    // ── POST /api/notifications/undo?userId=1 ────────────────────────────────

    /**
     * Deshace la última acción del usuario sobre sus notificaciones.
     * El frontend llama a este endpoint cuando el usuario pulsa "Deshacer"
     * en el toast que aparece tras cada acción.
     */
    @PostMapping("/undo")
    public ResponseEntity<Map<String, Object>> deshacer(@RequestParam Long userId) {
        return invoker.deshacerUltimo(userId)
            .map(cmd -> {
                Map<String, Object> resp = new HashMap<>();
                resp.put("mensaje",    "Acción deshecha: " + cmd.getDescripcion());
                resp.put("deshecho",   true);
                return ResponseEntity.ok(resp);
            })
            .orElseGet(() -> {
                Map<String, Object> resp = new HashMap<>();
                resp.put("mensaje",  "No hay acciones para deshacer");
                resp.put("deshecho", false);
                return ResponseEntity.ok(resp);
            });
    }

    // ── GET /api/notifications/stream?userId=1 ───────────────────────────────

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestParam Long userId) {
        return notificationService.suscribir(userId);
    }
}