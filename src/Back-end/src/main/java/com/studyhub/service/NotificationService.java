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

    /**
     * Mapa de emitters SSE activos: userId → lista de conexiones abiertas.
     * ConcurrentHashMap + CopyOnWriteArrayList para ser thread-safe.
     */
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public NotificationService(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    // ── SSE: suscribir un cliente ────────────────────────────────────────────

    public SseEmitter suscribir(Long userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // sin timeout

        emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        // Limpiar cuando la conexión se cierre o falle
        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(()    -> removeEmitter(userId, emitter));
        emitter.onError(e       -> removeEmitter(userId, emitter));

        return emitter;
    }

    private void removeEmitter(Long userId, SseEmitter emitter) {
        List<SseEmitter> lista = emitters.get(userId);
        if (lista != null) lista.remove(emitter);
    }

    // ── Publicar notificación (llamado por otros módulos) ────────────────────

    /**
     * Persiste la notificación en BD y la envía por SSE si el usuario está conectado.
     *
     * @param userId    ID del usuario destinatario
     * @param type      Tipo: "TAREA", "CALIFICACION", "SISTEMA", etc.
     * @param message   Texto visible
     * @param priority  "NORMAL" o "CRITICA"
     * @param actionUrl URL del recurso asociado (puede ser null)
     */
    public Notification publicar(Long userId, String type, String message,
                                  String priority, String actionUrl) {
        // 1. Persistir en BD
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setMessage(message);
        n.setPriority(priority != null ? priority : "NORMAL");
        n.setActionUrl(actionUrl);
        Notification guardada = notificationRepo.save(n);

        // 2. Enviar por SSE (con reintento si falla)
        enviarSSE(userId, guardada);

        return guardada;
    }

    private void enviarSSE(Long userId, Notification notif) {
        List<SseEmitter> lista = emitters.getOrDefault(userId, List.of());
        List<SseEmitter> muertos = new CopyOnWriteArrayList<>();

        for (SseEmitter emitter : lista) {
            try {
                emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(notif));
            } catch (IOException e) {
                // Emitter muerto — lo marcamos para remover
                muertos.add(emitter);
            }
        }
        lista.removeAll(muertos);
    }
}
