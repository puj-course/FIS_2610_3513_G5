package com.studyhub.controller;

import com.studyhub.model.Asignatura;
import com.studyhub.repository.AsignaturaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schedules")
@CrossOrigin(origins = "*")
public class ScheduleController {

    private final AsignaturaRepository asignaturaRepository;
    private static final List<String> VALID_DAYS = Arrays.asList("Lunes", "Martes", "Miércoles", "Miercoles", "Jueves", "Viernes", "Sábado", "Sabado", "Domingo", "Lun", "Mar", "Mié", "Mie", "Jue", "Vie", "Sáb", "Sab", "Dom");

    public ScheduleController(AsignaturaRepository asignaturaRepository) {
        this.asignaturaRepository = asignaturaRepository;
    }

    @GetMapping
    public ResponseEntity<?> getSchedules(
            @RequestParam(required = false) String day,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String keyword) {

        LocalTime parsedStartTime = null;
        LocalTime parsedEndTime = null;

        // Validation for Day
        if (day != null && !day.trim().isEmpty()) {
            boolean isValidDay = false;
            for (String d : VALID_DAYS) {
                if (d.equalsIgnoreCase(day.trim())) {
                    isValidDay = true;
                    break;
                }
            }
            if (!isValidDay) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Día inválido. Los valores permitidos son días de la semana.");
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }
        }

        // Validation for Time
        try {
            if (startTime != null && !startTime.trim().isEmpty()) {
                parsedStartTime = LocalTime.parse(startTime);
            }
            if (endTime != null && !endTime.trim().isEmpty()) {
                parsedEndTime = LocalTime.parse(endTime);
            }
        } catch (DateTimeParseException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Formato de hora inválido. Debe ser HH:mm");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null; // Ignore empty strings
        }
        
        if (day != null && day.trim().isEmpty()) {
            day = null;
        }

        List<Asignatura> results = asignaturaRepository.findSchedules(day, parsedStartTime, parsedEndTime, keyword);

        return new ResponseEntity<>(results, HttpStatus.OK);
    }
}
