package com.studyhub.service.observer;

import com.studyhub.model.Nota;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Subject del patrón Observer.
 * Mantiene el registro de observadores y dispara el evento
 * onNotaRegistrada cuando NotaService guarda una nueva nota.
 */
@Service
public class NotaEventPublisher {

    private final List<NotaObserver> observers = new CopyOnWriteArrayList<>();

    // ── Gestión de observers ────────────────────────────────────────────────

    public void registrar(NotaObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void desregistrar(NotaObserver observer) {
        observers.remove(observer);
    }

    // ── Publicación del evento ──────────────────────────────────────────────

    /**
     * @param nota             nota recién persistida
     * @param promedioAnterior promedio de la asignatura antes de guardar
     * @param promedioNuevo    promedio de la asignatura después de guardar
     * @param userId           ID del usuario dueño de la asignatura (resuelto desde BD)
     * @param nombreMateria    nombre de la asignatura (resuelto desde BD)
     */
    public void notificar(Nota nota, double promedioAnterior, double promedioNuevo,
                          Long userId, String nombreMateria) {
        for (NotaObserver observer : observers) {
            observer.onNotaRegistrada(nota, promedioAnterior, promedioNuevo, userId, nombreMateria);
        }
    }
}