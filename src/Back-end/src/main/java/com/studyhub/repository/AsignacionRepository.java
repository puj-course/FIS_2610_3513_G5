package com.studyhub.repository;

import com.studyhub.model.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {
    List<Asignacion> findByUsuarioId(Long usuarioId);
    List<Asignacion> findByCronogramaId(Long cronogramaId);
}
