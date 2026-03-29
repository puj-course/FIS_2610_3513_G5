package com.studyhub.service;

import com.studyhub.model.Calificacion;
import com.studyhub.repository.CalificacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CalificacionService {

    private final CalificacionRepository calificacionRepository;

    public CalificacionService(CalificacionRepository calificacionRepository) {
        this.calificacionRepository = calificacionRepository;
    }

    public Calificacion agregarCalificacion(Calificacion calificacion) {
        return calificacionRepository.save(calificacion);
    }

    public List<Calificacion> obtenerTodas() {
        return calificacionRepository.findAll();
    }

    public Optional<Calificacion> obtenerPorId(Long id) {
        return calificacionRepository.findById(id);
    }

    public Optional<Calificacion> actualizarCalificacion(Long id, Calificacion nuevosDatos) {
        return calificacionRepository.findById(id).map(calificacionExistente -> {

            if (nuevosDatos.getNombre() != null && !nuevosDatos.getNombre().trim().isEmpty()) {
                calificacionExistente.setNombre(nuevosDatos.getNombre());
            }
            if (nuevosDatos.getCalificacion() != null) {
                calificacionExistente.setCalificacion(nuevosDatos.getCalificacion());
            }
            if (nuevosDatos.getPorcentaje() != null) {
                calificacionExistente.setPorcentaje(nuevosDatos.getPorcentaje());
            }
            if (nuevosDatos.getAsignatura() != null) {
                calificacionExistente.setAsignatura(nuevosDatos.getAsignatura());
            }
            
            return calificacionRepository.save(calificacionExistente);
        });
    }

    public boolean eliminarCalificacion(Long id) {
        if (calificacionRepository.existsById(id)) {
            calificacionRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
