package com.studyhub.service;

import com.studyhub.model.Asignatura;
import com.studyhub.repository.AsignaturaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsignaturaService {

    private final AsignaturaRepository asignaturaRepository;

    public AsignaturaService(AsignaturaRepository asignaturaRepository) {
        this.asignaturaRepository = asignaturaRepository;
    }

    public List<Asignatura> findByUserId(Long userId) {
        return asignaturaRepository.findByUsuarioId(userId);
    }
}