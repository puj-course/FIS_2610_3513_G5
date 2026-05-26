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
import com.studyhub.dto.horarioDTO;
import com.studyhub.service.facade.horarioFacade;

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
    private final horarioFacade horarioFacade;

    public AsignaturaController(AsignaturaRepository asignaturaRepository,
                                AsignaturaService asignaturaService,
                                UsuarioRepository usuarioRepository,
                                NotaService notaService, horarioFacade horarioFacade) {
        this.asignaturaRepository = asignaturaRepository;
        this.asignaturaService    = asignaturaService;
        this.usuarioRepository    = usuarioRepository;
        this.notaService          = notaService;
        this.horarioFacade        = horarioFacade;
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

        String codigo = body.get("codigo").toString();
        if (asignaturaRepository.existsByCodigoAndUsuarioId(codigo, usuarioId)) {
            Map<String, Object> error = new HashMap<>();
            error.put("mensaje", "Ya existe una materia con el código " + codigo);
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        Asignatura asignatura = new Asignatura();
        asignatura.setNombre(body.get("nombre").toString());
        asignatura.setCodigo(body.get("codigo").toString());
        asignatura.setProfesor(body.get("profesor").toString());
        String horarioRaw = body.get("horario").toString();
        asignatura.setHorarioTexto(horarioRaw);

        // Parsear "Lun,Mié|11:00-13:00" → diasClase="Lun,Mié", horaInicio="11:00", horaFin="13:00"
        // Si el front envía los campos separados se usan directamente; si no, se extraen del string.
        if (body.get("diasClase") != null) {
            asignatura.setDiasClase(body.get("diasClase").toString());
        } else if (horarioRaw.contains("|")) {
            asignatura.setDiasClase(horarioRaw.split("\\|")[0]);
        }

        if (body.get("horaInicio") != null) {
            asignatura.setHoraInicio(body.get("horaInicio").toString());
        } else if (horarioRaw.contains("|") && horarioRaw.split("\\|").length > 1) {
            String franja = horarioRaw.split("\\|")[1]; // "11:00-13:00"
            asignatura.setHoraInicio(franja.split("-")[0].trim());
        }

        if (body.get("horaFin") != null) {
            asignatura.setHoraFin(body.get("horaFin").toString());
        } else if (horarioRaw.contains("|") && horarioRaw.split("\\|").length > 1) {
            String franja = horarioRaw.split("\\|")[1];
            String[] partes = franja.split("-");
            if (partes.length > 1) asignatura.setHoraFin(partes[1].trim());
        }
        asignatura.setCreditos(Integer.parseInt(body.get("creditos").toString()));
        asignatura.setPeriodo(body.get("periodo").toString());
        asignatura.setUsuario(usuario);

        // 2. Validar cruce de horarios
        String newDiasClase = asignatura.getDiasClase();
        String newHoraInicioStr = asignatura.getHoraInicio();
        String newHoraFinStr = asignatura.getHoraFin();

        if (newDiasClase != null && newHoraInicioStr != null && newHoraFinStr != null) {
            try {
                java.time.format.DateTimeFormatter formatter = new java.time.format.DateTimeFormatterBuilder()
                        .appendPattern("H:mm[:ss]")
                        .toFormatter();
                java.time.LocalTime newStart = java.time.LocalTime.parse(newHoraInicioStr.trim(), formatter);
                java.time.LocalTime newEnd = java.time.LocalTime.parse(newHoraFinStr.trim(), formatter);

                List<Asignatura> existentes = asignaturaRepository.findByUsuarioId(usuarioId);
                for (Asignatura exist : existentes) {
                    if (exist.getDiasClase() != null && exist.getHoraInicio() != null && exist.getHoraFin() != null) {
                        boolean shareDays = false;
                        String[] daysNew = newDiasClase.split(",");
                        String[] daysExist = exist.getDiasClase().split(",");
                        for (String dNew : daysNew) {
                            for (String dExist : daysExist) {
                                if (dNew.trim().equalsIgnoreCase(dExist.trim())) {
                                    shareDays = true;
                                    break;
                                }
                            }
                            if (shareDays) break;
                        }

                        if (shareDays) {
                            java.time.LocalTime existStart = java.time.LocalTime.parse(exist.getHoraInicio().trim(), formatter);
                            java.time.LocalTime existEnd = java.time.LocalTime.parse(exist.getHoraFin().trim(), formatter);

                            // overlap condition: start1 < end2 && start2 < end1
                            if (newStart.isBefore(existEnd) && existStart.isBefore(newEnd)) {
                                Map<String, Object> error = new HashMap<>();
                                error.put("mensaje", "Cruce de horarios detectado con la materia: " + exist.getNombre());
                                return new ResponseEntity<>(error, HttpStatus.CONFLICT);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // If it fails to parse, we log and skip validation
                System.err.println("Error parsing time: " + e.getMessage());
            }
        }

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

        asignaturaService.eliminarAsignatura(id);

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
    
    @GetMapping("/horario")
    public ResponseEntity<List<horarioDTO>> obtenerHorario(@RequestParam Long usuarioId) {
        List<horarioDTO> horario = horarioFacade.obtenerHorarioCompleto(usuarioId);
        return new ResponseEntity<>(horario, HttpStatus.OK);
    }
}