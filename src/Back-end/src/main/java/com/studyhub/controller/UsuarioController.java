package com.studyhub.controller;

import com.studyhub.model.Usuario;
import com.studyhub.dto.EstadisticasDTO;
import com.studyhub.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        return usuarioService.crearUsuario(usuario);
    }

    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<EstadisticasDTO> obtenerEstadisticas(@PathVariable Long id) {
        EstadisticasDTO estadisticas = usuarioService.obtenerEstadisticas(id);
        return ResponseEntity.ok(estadisticas);
    }
}