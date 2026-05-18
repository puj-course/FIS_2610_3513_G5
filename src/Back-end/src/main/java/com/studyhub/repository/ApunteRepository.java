package com.studyhub.repository;

import com.studyhub.model.Apunte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ApunteRepository extends JpaRepository<Apunte, Long> {
}
