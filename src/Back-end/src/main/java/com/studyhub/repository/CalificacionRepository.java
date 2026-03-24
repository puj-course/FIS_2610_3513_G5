package com.studyhub.repository;

import com.studyhub.model.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {
    
    List<Calificacion> findByAsignaturaId(Long asignaturaId);

}
