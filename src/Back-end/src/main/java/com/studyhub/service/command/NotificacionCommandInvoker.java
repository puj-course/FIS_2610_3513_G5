package com.studyhub.service.command;

import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Invoker del patrón Command.
 *
 * Mantiene una pila de comandos ejecutados por usuario (userId).
 * Cada usuario tiene su propia pila para que el undo sea independiente.
 *
 * Límite por pila: 20 comandos (evita crecimiento ilimitado en sesiones largas).
 */
@Service
public class NotificacionCommandInvoker {

    private static final int MAX_PILA = 20;

    /** Pila de comandos por usuario. */
    private final Map<Long, Deque<NotificacionCommand>> pilas = new ConcurrentHashMap<>();

    /**
     * Ejecuta el comando y lo apila para posible undo.
     *
     * @param userId  ID del usuario que realiza la acción.
     * @param comando Comando a ejecutar.
     */
    public void ejecutar(Long userId, NotificacionCommand comando) {
        comando.ejecutar();

        Deque<NotificacionCommand> pila = pilas.computeIfAbsent(
            userId, k -> new LinkedBlockingDeque<>(MAX_PILA)
        );

        // Si la pila está llena, descarta el más antiguo (fondo)
        if (pila.size() >= MAX_PILA) {
            pila.pollLast();
        }
        pila.push(comando);   // push al tope (más reciente primero)
    }

    /**
     * Deshace el último comando ejecutado por el usuario.
     *
     * @param userId ID del usuario.
     * @return El comando que fue deshecho, o {@link Optional#empty()} si no hay nada que deshacer.
     */
    public Optional<NotificacionCommand> deshacerUltimo(Long userId) {
        Deque<NotificacionCommand> pila = pilas.get(userId);
        if (pila == null || pila.isEmpty()) {
            return Optional.empty();
        }

        NotificacionCommand comando = pila.pop();
        comando.deshacer();
        return Optional.of(comando);
    }

    /** Indica si el usuario tiene acciones que se puedan deshacer. */
    public boolean tieneHistorial(Long userId) {
        Deque<NotificacionCommand> pila = pilas.get(userId);
        return pila != null && !pila.isEmpty();
    }
}