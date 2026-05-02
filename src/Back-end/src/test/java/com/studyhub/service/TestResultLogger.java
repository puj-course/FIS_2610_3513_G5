package com.studyhub.service;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

public class TestResultLogger implements TestWatcher {

    @Override
    public void testSuccessful(ExtensionContext context) {
        System.err.printf("%-6s | %-75s | PASSED%n",
                getCp(context.getDisplayName()),
                context.getDisplayName());
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        System.err.printf("%-6s | %-75s | FAILED - %s%n",
                getCp(context.getDisplayName()),
                context.getDisplayName(),
                cause.getMessage());
    }

    private String getCp(String name) {
        // ─── NotaService ───────────────────────────────────────────────────
        if (name.contains("DatosValidos") && name.contains("agregar"))           return "CP01";
        if (name.contains("Negativo"))                                            return "CP02";
        if (name.contains("SumaSuperaCien"))                                      return "CP03";
        if (name.contains("ExactamenteCien") && name.contains("Porcentaje"))     return "CP04";
        if (name.contains("CientoUno"))                                           return "CP05";
        if (name.contains("calcularPromedio") || name.contains("Promedio"))      return "CP06";
        if (name.contains("retornaCero") && name.contains("Notas"))              return "CP07";

        // ─── EstadoTareaService ────────────────────────────────────────────
        if (name.contains("Vencida"))                                             return "CP08";
        if (name.contains("Proxima") || name.contains("Manana"))                 return "CP09";
        if (name.contains("Pendiente"))                                           return "CP10";

        // ─── ResenaService ─────────────────────────────────────────────────
        if (name.contains("Vacio") || name.contains("Nulo") || name.contains("Espacios")) return "CP11";
        if (name.contains("MayorQueCinco") || name.contains("MenorQueUno")
                || name.contains("CalificacionEsUno") || name.contains("Quinientos"))     return "CP12";
        if (name.contains("NoDueno") || name.contains("EsDueno")
                || name.contains("ResenaNoExiste"))                               return "CP13";

        return "—";
    }
}