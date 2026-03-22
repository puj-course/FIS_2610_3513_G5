package com.studyhub.service;

import com.studyhub.model.Nota;
import com.studyhub.repository.NotaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotaService {

    private final NotaRepository notaRepository;

    public NotaService(NotaRepository notaRepository) {
        this.notaRepository = notaRepository;
    }

    public Nota agregarNota(Nota nota) {
        if (nota.getPorcentaje() < 0 || nota.getPorcentaje() > 100) {
            throw new RuntimeException("El porcentaje debe estar entre 0 y 100");
        }
        List<Nota> notas = notaRepository.findByAsignaturaId(nota.getAsignatura().getId());

        double suma = 0.0;
        for (Nota n : notas) {
            suma += n.getPorcentaje();
        }
        if (suma + nota.getPorcentaje() > 100) {
            throw new RuntimeException("La suma de porcentajes no puede superar el 100%");
        }

        return notaRepository.save(nota);
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
            notaExistente.setNombre(datosActualizados.getNombre());
            notaExistente.setCalificacion(datosActualizados.getCalificacion());
            notaExistente.setPorcentaje(datosActualizados.getPorcentaje());
            if (datosActualizados.getAsignatura() != null) {
                notaExistente.setAsignatura(datosActualizados.getAsignatura());
            }
            return notaRepository.save(notaExistente);
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

        if (notas.isEmpty()) {
            return 0.0;
        }

        double suma = 0.0;

        for (Nota nota : notas) {
            double calificacion = nota.getCalificacion();
            double porcentaje = nota.getPorcentaje();

            suma += calificacion * (porcentaje / 100);
        }

        return suma;
    }
}
