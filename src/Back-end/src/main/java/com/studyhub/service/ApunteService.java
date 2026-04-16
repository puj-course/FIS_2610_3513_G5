package com.studyhub.service;

import com.studyhub.model.Apunte;
import com.studyhub.repository.ApunteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApunteService {

    @Autowired
    private ApunteRepository apunteRepository;

    public Apunte crearApunte(Apunte apunte) {
        return apunteRepository.save(apunte);
    }

    public List<Apunte> listarApuntes() {
        return apunteRepository.findAll();
    }

    public Optional<Apunte> obtenerApunte(Long id) {
        return apunteRepository.findById(id);
    }

    public Apunte actualizarApunte(Long id, Apunte apunteActualizado) {
        return apunteRepository.findById(id).map(apunte -> {
            apunte.setTitulo(apunteActualizado.getTitulo());
            apunte.setContenido(apunteActualizado.getContenido());
            // No actualizamos la fechaCreacion
            return apunteRepository.save(apunte);
        }).orElseThrow(() -> new RuntimeException("Apunte no encontrado con ID: " + id));
    }

    public void eliminarApunte(Long id) {
        apunteRepository.deleteById(id);
    }
}
