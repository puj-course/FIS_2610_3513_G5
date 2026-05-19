package com.studyhub.service.command;

/**
 * Interfaz del patrón Command para acciones sobre notificaciones.
 * Cada implementación encapsula una acción reversible.
 */
public interface NotificacionCommand {

    /** Ejecuta la acción principal. */
    void ejecutar();

    /** Revierte la acción ejecutada. */
    void deshacer();

    /** Descripción legible de la acción, usada en el toast de undo. */
    String getDescripcion();
}