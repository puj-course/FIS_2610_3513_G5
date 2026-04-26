package com.studyhub.repository;

import com.studyhub.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);
    
    Optional<Usuario> findByTokenRecuperacion(String token);
    

}