package com.studyhub.repository;

import com.studyhub.model.Asignatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface AsignaturaRepository extends JpaRepository<Asignatura, Long> {
    List<Asignatura> findByUsuarioId(Long usuarioId);
    
    @Query("SELECT a FROM Asignatura a WHERE " +
           "(:day IS NULL OR LOWER(a.diasClase) LIKE LOWER(CONCAT('%', :day, '%'))) AND " +
           "(:startTime IS NULL OR a.horaInicio >= :startTime) AND " +
           "(:endTime IS NULL OR a.horaFin <= :endTime) AND " +
           "(:keyword IS NULL OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.codigo) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Asignatura> findSchedules(@Param("day") String day,
                                   @Param("startTime") String startTime,
                                   @Param("endTime") String endTime,
                                   @Param("keyword") String keyword);
}

