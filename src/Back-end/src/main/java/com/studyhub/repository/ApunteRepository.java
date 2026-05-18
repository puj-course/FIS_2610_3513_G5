package com.studyhub.repository;

import com.studyhub.model.Apunte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApunteRepository extends JpaRepository<Apunte, Long> {
    List<Apunte> findByAsignatura_Usuario_Id(Long usuarioId);
}
