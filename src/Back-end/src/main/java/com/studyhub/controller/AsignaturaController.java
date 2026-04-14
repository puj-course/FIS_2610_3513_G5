package com.studyhub.controller;

import com.studyhub.model.Asignatura;
import com.studyhub.model.Usuario;
import com.studyhub.repository.AsignaturaRepository;
import com.studyhub.repository.UsuarioRepository;
import com.studyhub.service.AsignaturaService;
import com.studyhub.service.NotaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/asignaturas")
@CrossOrigin(origins = "*")
public class AsignaturaController {

    private final AsignaturaRepository asignaturaRepository;
    private final AsignaturaService asignaturaService;
    private final UsuarioRepository usuarioRepository;
    private final NotaService notaService;

    public AsignaturaController(AsignaturaRepository asignaturaRepository,
                                AsignaturaService asignaturaService,
                                UsuarioRepository usuarioRepository,
                                NotaService notaService) {
        this.asignaturaRepository = asignaturaRepository;
        this.asignaturaService    = asignaturaService;
        this.usuarioRepository    = usuarioRepository;
        this.notaService          = notaService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearAsignatura(@RequestBody Map<String, Object> body) {
        Long usuarioId = body.get("usuarioId") != null
                ? Long.valueOf(body.get("usuarioId").toString())
                : null;

        if (usuarioId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("mensaje", "El usuarioId es obligatorio");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("mensaje", "Usuario no encontrado");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        Asignatura asignatura = new Asignatura();
        asignatura.setNombre(body.get("nombre").toString());
        asignatura.setCodigo(body.get("codigo").toString());
        asignatura.setProfesor(body.get("profesor").toString());
        asignatura.setHorario(body.get("horario").toString());
        asignatura.setCreditos(Integer.parseInt(body.get("creditos").toString()));
        asignatura.setPeriodo(body.get("periodo").toString());
        asignatura.setUsuario(usuario);

        Asignatura guardada = asignaturaRepository.save(asignatura);
        System.out.println("Asignatura guardada en BD: " + guardada);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Asignatura guardada exitosamente");
        respuesta.put("asignatura", guardada);
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Asignatura>> listarAsignaturas(@RequestParam(required = false) Long usuarioId) {
        List<Asignatura> asignaturas = usuarioId != null
                ? asignaturaService.findByUserId(usuarioId)
                : asignaturaRepository.findAll();
        
        // Calcular progreso para cada asignatura
        for (Asignatura asignatura : asignaturas) {
            asignatura.setProgreso(notaService.calcularProgreso(asignatura.getId()));
        }
        
        return new ResponseEntity<>(asignaturas, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarAsignatura(@PathVariable Long id) {
        if (!asignaturaRepository.existsById(id)) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Asignatura no encontrada");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        asignaturaRepository.deleteById(id);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Asignatura eliminada exitosamente");
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    @GetMapping("/{id}/promedio")
    public ResponseEntity<Map<String, Object>> obtenerPromedioAsignatura(@PathVariable Long id) {
        Asignatura asignatura = asignaturaRepository.findById(id).orElse(null);

        if (asignatura == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("mensaje", "Asignatura no encontrada");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        double promedio = notaService.calcularPromedio(id);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("asignatura", asignatura.getNombre());
        respuesta.put("promedio", promedio);
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }
}