package com.studyhub.service;

import com.studyhub.model.Asignatura;
import com.studyhub.repository.AsignaturaRepository;
import com.studyhub.repository.NotaRepository;
import com.studyhub.repository.TareaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AsignaturaService {

    private final AsignaturaRepository asignaturaRepository;
    private final NotaRepository notaRepository;
    private final TareaRepository tareaRepository;

    public AsignaturaService(AsignaturaRepository asignaturaRepository,
                             NotaRepository notaRepository,
                             TareaRepository tareaRepository) {
        this.asignaturaRepository = asignaturaRepository;
        this.notaRepository = notaRepository;
        this.tareaRepository = tareaRepository;
    }

    public List<Asignatura> findByUserId(Long userId) {
        return asignaturaRepository.findByUsuarioId(userId);
    }

    @Transactional
    public void eliminarAsignatura(Long id) {
        tareaRepository.deleteAll(tareaRepository.findByAsignaturaId(id));
        notaRepository.deleteAll(notaRepository.findByAsignaturaId(id));
        asignaturaRepository.deleteById(id);
    }
}