package com.studyhub.repository;

import com.studyhub.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {
    
    List<Nota> findByAsignaturaId(Long asignaturaId);
    List<Nota> findByAsignatura_Usuario_Id(Long usuarioId);

}
