package com.studyhub.service;

import com.studyhub.model.Notification;
import com.studyhub.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepo;

    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public NotificationService(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    // ── SSE: suscribir un cliente ────────────────────────────────────────────

    public SseEmitter suscribir(Long userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(()    -> removeEmitter(userId, emitter));
        emitter.onError(e       -> removeEmitter(userId, emitter));

        return emitter;
    }

    private void removeEmitter(Long userId, SseEmitter emitter) {
        List<SseEmitter> lista = emitters.get(userId);
        if (lista != null) lista.remove(emitter);
    }

    // ── Publicar notificación (parámetros sueltos — método original) ─────────

    public Notification publicar(Long userId, String type, String message,
                                  String priority, String actionUrl) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setMessage(message);
        n.setPriority(priority != null ? priority : "NORMAL");
        n.setActionUrl(actionUrl);
        Notification guardada = notificationRepo.save(n);

        enviarSSE(userId, guardada);
        return guardada;
    }

    // ── Publicar notificación (objeto ya construido por una Factory) ─────────

    public Notification publicar(Notification n) {
        Notification guardada = notificationRepo.save(n);
        enviarSSE(n.getUserId(), guardada);
        return guardada;
    }

    // ── Envío SSE ────────────────────────────────────────────────────────────

    private void enviarSSE(Long userId, Notification notif) {
        List<SseEmitter> lista = emitters.getOrDefault(userId, new CopyOnWriteArrayList<>());
        List<SseEmitter> muertos = new CopyOnWriteArrayList<>();

        for (SseEmitter emitter : lista) {
            try {
                emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(notif));
            } catch (IOException e) {
                muertos.add(emitter);
            }
        }
        lista.removeAll(muertos);
    }
}