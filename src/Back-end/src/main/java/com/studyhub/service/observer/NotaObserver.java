package com.studyhub.service.observer;

import com.studyhub.model.Nota;

/**
 * Interfaz Observer del patrón ObserverSistemaAlertas.
 *
 * userId y nombreMateria se pasan ya resueltos desde NotaService
 * (cargados desde AsignaturaRepository) para evitar LazyInitializationException
 * cuando la nota llega desde el frontend con asignatura parcial {id: X}.
 */
public interface NotaObserver {

    /**
     * @param nota             nota recién guardada
     * @param promedioAnterior promedio ponderado ANTES de guardar
     * @param promedioNuevo    promedio ponderado DESPUÉS de guardar
     * @param userId           ID del dueño de la asignatura (ya resuelto desde BD)
     * @param nombreMateria    nombre de la asignatura (ya resuelto desde BD)
     */
    void onNotaRegistrada(Nota nota, double promedioAnterior, double promedioNuevo,
                          Long userId, String nombreMateria);
}