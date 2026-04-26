package com.studyhub.service;

import com.studyhub.model.Resena;
import com.studyhub.repository.ResenaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResenaService {

    private final ResenaRepository resenaRepository;

    public ResenaService(ResenaRepository resenaRepository) {
        this.resenaRepository = resenaRepository;
    }

    public Resena crearResena(Resena resena) {
        if (resena.getComentario() == null || resena.getComentario().isBlank()) {
            throw new RuntimeException("El comentario no puede estar vacío");
        }
        if (resena.getComentario().length() > 500) {
            throw new RuntimeException("El comentario no puede superar los 500 caracteres");
        }
        if (resena.getCalificacion() < 1 || resena.getCalificacion() > 5) {
            throw new RuntimeException("La calificación debe estar entre 1 y 5");
        }
        return resenaRepository.save(resena);
    }

    public Page<Resena> obtenerResenas(Pageable pageable) {
        return resenaRepository.findAll(pageable);
    }

    public Optional<Resena> obtenerPorId(Long id) {
        return resenaRepository.findById(id);
    }

    public void eliminarResena(Long id, Long usuarioId) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada"));
        
        if (!resena.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para eliminar esta reseña");
        }
        
        resenaRepository.delete(resena);
    }
}
