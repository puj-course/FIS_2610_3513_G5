package com.studyhub.repository;

import com.studyhub.model.Cronograma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CronogramaRepository extends JpaRepository<Cronograma, Long> {
    Optional<Cronograma> findByFechaInicioSemana(LocalDate fecha);
}
