package com.studyhub.service;

import com.studyhub.model.Asignatura;
import com.studyhub.model.Nota;
import com.studyhub.repository.AsignaturaRepository;
import com.studyhub.repository.NotaRepository;
import com.studyhub.service.observer.NotaEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotaService {

    private final NotaRepository notaRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final NotaEventPublisher notaEventPublisher;

    public NotaService(NotaRepository notaRepository,
            AsignaturaRepository asignaturaRepository,
            NotaEventPublisher notaEventPublisher) {
        this.notaRepository = notaRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.notaEventPublisher = notaEventPublisher;
    }

    public Nota agregarNota(Nota nota) {
        if (nota.getPorcentaje() < 0 || nota.getPorcentaje() > 100) {
            throw new RuntimeException("El porcentaje debe estar entre 0 y 100");
        }

        Long asignaturaId = nota.getAsignatura().getId();
        List<Nota> notas = notaRepository.findByAsignaturaId(asignaturaId);

        double suma = 0.0;
        for (Nota n : notas) {
            suma += n.getPorcentaje();
        }
        if (suma + nota.getPorcentaje() > 100) {
            throw new RuntimeException("La suma de porcentajes no puede superar el 100%");
        }

        // Resolver asignatura completa desde BD
        Asignatura asignatura = resolverAsignatura(asignaturaId);
        Long userId = asignatura.getUsuario().getId();
        String nombreMateria = asignatura.getNombre();

        double promedioAnterior = calcularPromedioDesde(notas);

        Nota notaGuardada = notaRepository.save(nota);

        List<Nota> notasActualizadas = notaRepository.findByAsignaturaId(asignaturaId);
        double promedioNuevo = calcularPromedioDesde(notasActualizadas);

        notaEventPublisher.notificar(notaGuardada, promedioAnterior, promedioNuevo,
                userId, nombreMateria);

        return notaGuardada;
    }

    public List<Nota> obtenerTodasLasNotas() {
        return notaRepository.findAll();
    }

    public Optional<Nota> obtenerNotaPorId(Long id) {
        return notaRepository.findById(id);
    }

    public List<Nota> obtenerNotasPorAsignatura(Long asignaturaId) {
        return notaRepository.findByAsignaturaId(asignaturaId);
    }

    public Optional<Nota> actualizarNota(Long id, Nota datosActualizados) {
        return notaRepository.findById(id).map(notaExistente -> {

            Long asignaturaId = notaExistente.getAsignatura().getId();

            // Resolver asignatura completa desde BD
            Asignatura asignatura = resolverAsignatura(asignaturaId);
            Long userId = asignatura.getUsuario().getId();
            String nombreMateria = asignatura.getNombre();

            // Promedio ANTES
            List<Nota> notasAntes = notaRepository.findByAsignaturaId(asignaturaId);
            double promedioAnterior = calcularPromedioDesde(notasAntes);

            notaExistente.setNombre(datosActualizados.getNombre());
            notaExistente.setCalificacion(datosActualizados.getCalificacion());
            notaExistente.setPorcentaje(datosActualizados.getPorcentaje());
            if (datosActualizados.getAsignatura() != null) {
                notaExistente.setAsignatura(datosActualizados.getAsignatura());
            }
            Nota notaGuardada = notaRepository.save(notaExistente);

            // Promedio DESPUÉS
            List<Nota> notasDespues = notaRepository.findByAsignaturaId(asignaturaId);
            double promedioNuevo = calcularPromedioDesde(notasDespues);

            // LOG TEMPORAL
            System.out.println(">>> promedioAnterior=" + promedioAnterior
                    + " promedioNuevo=" + promedioNuevo
                    + " userId=" + userId);

            notaEventPublisher.notificar(notaGuardada, promedioAnterior, promedioNuevo,
                    userId, nombreMateria);

            return notaGuardada;
        });
    }

    public boolean eliminarNota(Long id) {
        if (notaRepository.existsById(id)) {
            notaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public double calcularPromedio(Long asignaturaId) {
        List<Nota> notas = notaRepository.findByAsignaturaId(asignaturaId);
        return calcularPromedioDesde(notas);
    }

    public double calcularProgreso(Long asignaturaId) {
        List<Nota> notas = notaRepository.findByAsignaturaId(asignaturaId);
        if (notas.isEmpty())
            return 0.0;

        double suma = 0.0;
        for (Nota nota : notas) {
            if (nota.getCalificacion() != null) {
                suma += nota.getPorcentaje();
            }
        }
        return suma;
    }

    // ── Métodos internos ──────────────────────────────────────────────────────

    private Asignatura resolverAsignatura(Long asignaturaId) {
        return asignaturaRepository.findById(asignaturaId)
                .orElseThrow(() -> new RuntimeException("Asignatura no encontrada: " + asignaturaId));
    }

    private double calcularPromedioDesde(List<Nota> notas) {
        if (notas.isEmpty())
            return 0.0;

        double sumaNotas = 0.0;
        double sumaPorcentajes = 0.0;
        for (Nota nota : notas) {
            if (nota.getCalificacion() == null)
                continue;
            sumaNotas += nota.getCalificacion() * (nota.getPorcentaje() / 100.0);
            sumaPorcentajes += nota.getPorcentaje() / 100.0;
        }
        if (sumaPorcentajes == 0) return 0.0;
        return sumaNotas / sumaPorcentajes;
    }
}