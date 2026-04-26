package com.studyhub.repository;

import com.studyhub.model.SesionInvalidada;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface SesionInvalidadaRepository extends JpaRepository<SesionInvalidada, Long> {

    // ¿Existe algún logout posterior al momento en que el cliente hizo login?
    boolean existsByUsuarioIdAndFechaLogoutAfter(Long usuarioId, LocalDateTime loginAt);
}
