package com.studyhub.service.observer;

import com.studyhub.model.Nota;
import com.studyhub.model.Notification;
import com.studyhub.service.NotificationService;
import com.studyhub.service.factory.NotificacionMateriaEnRiesgoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Observer unificado del patrón ObserverSistemaAlertas.
 *
 * Responsabilidades:
 *  1. AlertaRiesgo: si el promedio cruzó de >= 3.0 a < 3.0, genera una
 *     notificación CRÍTICA mediante NotificacionMateriaEnRiesgoFactory.
 *  2. Estadísticas: mantiene en memoria el mapa userId → Set<materia>
 *     de materias actualmente en riesgo, consultable por el dashboard.
 *
 * Se auto-registra en NotaEventPublisher vía constructor.
 */
@Component
public class SistemaAlertasObserver implements NotaObserver {

    private static final Logger log = LoggerFactory.getLogger(SistemaAlertasObserver.class);
    private static final double UMBRAL = 3.0;

    private final NotificationService notificationService;
    private final NotificacionMateriaEnRiesgoFactory factory;

    // userId -> conjunto de nombres de materias en riesgo
    private final Map<Long, Set<String>> materiasEnRiesgo = new ConcurrentHashMap<>();

    public SistemaAlertasObserver(NotaEventPublisher publisher,
                                   NotificationService notificationService) {
        this.notificationService = notificationService;
        this.factory = new NotificacionMateriaEnRiesgoFactory();
        publisher.registrar(this);
    }

    @Override
    public void onNotaRegistrada(Nota nota, double promedioAnterior, double promedioNuevo,
                                  Long userId, String nombreMateria) {

        // ── 1. Actualizar mapa de estadísticas ────────────────────────────
        if (promedioNuevo < UMBRAL) {
            materiasEnRiesgo
                .computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet())
                .add(nombreMateria);
            log.info("SistemaAlertasObserver: '{}' en riesgo para usuario {} (promedio: {})",
                    nombreMateria, userId, promedioNuevo);
        } else {
            Set<String> set = materiasEnRiesgo.get(userId);
            if (set != null) {
                set.remove(nombreMateria);
                log.info("SistemaAlertasObserver: '{}' retirada de riesgo para usuario {}",
                        nombreMateria, userId);
            }
        }

        // ── 2. Disparar alerta solo si cruzó el umbral hacia abajo ────────
        boolean cruzaUmbral = promedioAnterior >= UMBRAL && promedioNuevo < UMBRAL;
        if (!cruzaUmbral) return;

        double promedioRedondeado = Math.round(promedioNuevo * 100.0) / 100.0;
        log.warn("SistemaAlertasObserver: '{}' cruzó umbral ({} → {}) — notificando usuario {}",
                nombreMateria, promedioAnterior, promedioNuevo, userId);

        Notification notif = factory.crear(userId, nombreMateria, promedioRedondeado);
        notificationService.publicar(notif);
    }

    /**
     * Consulta las materias en riesgo conocidas para un usuario.
     * Usado por NotaController: GET /notas/materias-en-riesgo/{usuarioId}
     */
    public Set<String> obtenerMateriasEnRiesgo(Long userId) {
        return Collections.unmodifiableSet(
            materiasEnRiesgo.getOrDefault(userId, ConcurrentHashMap.newKeySet())
        );
    }
}