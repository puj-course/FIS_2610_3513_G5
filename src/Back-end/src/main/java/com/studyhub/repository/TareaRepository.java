package com.studyhub.repository;

import com.studyhub.model.Tarea;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {
	List<Tarea> findByFechaEntrega(java.time.LocalDate fechaEntrega);
	List<Tarea> findByAsignatura_Usuario_Id(Long usuarioId);
	List<Tarea> findByAsignatura_Usuario_IdAndEstadoTrueOrderByFechaEntregaAsc(Long usuarioId);
	List<Tarea> findByAsignatura_Usuario_IdAndFechaEntregaBetween(Long usuarioId, java.time.LocalDate startDate, java.time.LocalDate endDate);
	
}