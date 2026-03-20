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
}
