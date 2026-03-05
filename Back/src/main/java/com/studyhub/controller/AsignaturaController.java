package com.studyhub.controller;

// Importar nuestro modelo y repositorio
import com.studyhub.model.Asignatura;
import com.studyhub.repository.AsignaturaRepository;

// Importar clases de Spring Web
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Importar clases de Java para las respuestas
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * =====================================================================
 * CONTROLADOR REST — AsignaturaController
 * =====================================================================
 * 
 * Un Controller es la capa que RECIBE las solicitudes HTTP del front-end
 * y devuelve respuestas. Es como el "recepcionista" de un hotel:
 * - Recibe peticiones de los clientes (front-end)
 * - Pide al encargado que haga el trabajo (Repository)
 * - Devuelve la respuesta al cliente
 * 
 * FLUJO COMPLETO:
 *   Navegador/Front-end
 *       ↓ (HTTP Request: POST /asignaturas)
 *   AsignaturaController (recibe y procesa)
 *       ↓
 *   AsignaturaRepository (accede a la BD)
 *       ↓
 *   Base de Datos H2 (guarda/lee datos)
 *       ↓
 *   AsignaturaController (construye la respuesta)
 *       ↓ (HTTP Response: JSON)
 *   Navegador/Front-end (recibe y muestra)
 * 
 * ANOTACIONES CLAVE:
 * @RestController → "Soy un controlador que devuelve datos (JSON),
 *                    no páginas HTML"
 * @RequestMapping → "Todas mis rutas empiezan con /asignaturas"
 * @CrossOrigin    → "Acepto solicitudes desde otros orígenes (puertos)"
 *                    Necesario porque el front-end corre en un puerto
 *                    diferente al back-end (ej: 5500 vs 8080)
 */
@RestController
@RequestMapping("/asignaturas")
@CrossOrigin(origins = "*")  // "*" = aceptar desde cualquier origen
public class AsignaturaController {

    /**
     * Referencia al repositorio para acceder a la BD.
     * 'final' = no se puede cambiar después de crearse.
     */
    private final AsignaturaRepository asignaturaRepository;

    /**
     * INYECCIÓN DE DEPENDENCIAS POR CONSTRUCTOR
     * 
     * Spring Boot crea automáticamente una instancia de
     * AsignaturaRepository y la "inyecta" aquí.
     * No necesitas hacer "new AsignaturaRepository()" manualmente.
     * 
     * Esto es uno de los conceptos clave de Spring: tú declaras
     * qué necesitas, y Spring se encarga de crearlo y entregártelo.
     */
    public AsignaturaController(AsignaturaRepository asignaturaRepository) {
        this.asignaturaRepository = asignaturaRepository;
    }

    // =====================================================================
    // ENDPOINT: POST /asignaturas — Crear una asignatura nueva
    // =====================================================================
    /**
     * Recibe los datos de una asignatura desde el front-end (como JSON)
     * y la guarda en la base de datos H2.
     * 
     * @PostMapping → Este método responde a solicitudes HTTP POST
     * @RequestBody → "Convierte el JSON del body de la petición
     *                 a un objeto Java Asignatura automáticamente"
     * 
     * EJEMPLO DE PETICIÓN DEL FRONT-END:
     *   fetch('http://localhost:8080/asignaturas', {
     *       method: 'POST',
     *       headers: { 'Content-Type': 'application/json' },
     *       body: JSON.stringify({
     *           nombre: 'Cálculo',
     *           codigo: 'MAT101',
     *           profesor: 'Dr. García',
     *           horario: 'Lunes 8-10',
     *           creditos: 4,
     *           periodo: '2026-1'
     *       })
     *   });
     * 
     * EJEMPLO DE RESPUESTA (JSON):
     *   {
     *       "mensaje": "Asignatura guardada exitosamente",
     *       "asignatura": { "id": 1, "nombre": "Cálculo", ... }
     *   }
     * 
     * @param asignatura Objeto con los datos del formulario
     * @return ResponseEntity con la asignatura guardada (código 201 CREATED)
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearAsignatura(@RequestBody Asignatura asignatura) {
        
        // 1. Guardar en la base de datos H2 usando el repositorio
        //    .save() hace un INSERT INTO asignaturas VALUES (...)
        //    y devuelve el objeto con el ID generado por la BD
        Asignatura asignaturaGuardada = asignaturaRepository.save(asignatura);

        // 2. Imprimir en consola del servidor (para verificar que llegó)
        System.out.println("✅ Asignatura guardada en BD: " + asignaturaGuardada);

        // 3. Construir la respuesta JSON que se envía al front-end
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Asignatura guardada exitosamente");
        respuesta.put("asignatura", asignaturaGuardada);

        // 4. Devolver la respuesta con código HTTP 201 (CREATED)
        //    ResponseEntity nos permite controlar el código HTTP
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    // =====================================================================
    // ENDPOINT: GET /asignaturas — Listar todas las asignaturas
    // =====================================================================
    /**
     * Devuelve TODAS las asignaturas guardadas en la base de datos.
     * El front-end usa este endpoint al cargar la página para
     * mostrar las materias que ya fueron guardadas previamente.
     * 
     * @GetMapping → Este método responde a solicitudes HTTP GET
     * 
     * EJEMPLO: El navegador accede a http://localhost:8080/asignaturas
     * RESPUESTA:
     *   [
     *       { "id": 1, "nombre": "Cálculo", "codigo": "MAT101", ... },
     *       { "id": 2, "nombre": "Fund. Ing. Software", "codigo": "FIS2610", ... }
     *   ]
     * 
     * @return Lista de todas las asignaturas (código 200 OK)
     */
    @GetMapping
    public ResponseEntity<List<Asignatura>> listarAsignaturas() {
        // .findAll() hace: SELECT * FROM asignaturas
        List<Asignatura> asignaturas = asignaturaRepository.findAll();
        return new ResponseEntity<>(asignaturas, HttpStatus.OK);
    }

    // =====================================================================
    // ENDPOINT: DELETE /asignaturas/{id} — Eliminar una asignatura
    // =====================================================================
    /**
     * Elimina una asignatura por su ID.
     * Se usa cuando el usuario hace clic en "Eliminar" en una tarjeta.
     * 
     * @DeleteMapping("/{id}") → Responde a DELETE /asignaturas/5
     * @PathVariable → Extrae el {id} de la URL. Ejemplo:
     *                  DELETE /asignaturas/5 → id = 5
     * 
     * @param id ID de la asignatura a eliminar
     * @return Mensaje de confirmación o error 404 si no existe
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarAsignatura(@PathVariable Long id) {
        
        // 1. Verificar que la asignatura existe antes de eliminarla
        if (!asignaturaRepository.existsById(id)) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Asignatura no encontrada");
            // Devolver código 404 (NOT FOUND) si no existe
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        // 2. Eliminar de la base de datos
        //    .deleteById() hace: DELETE FROM asignaturas WHERE id = ?
        asignaturaRepository.deleteById(id);

        // 3. Confirmar la eliminación
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Asignatura eliminada exitosamente");
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }
}
