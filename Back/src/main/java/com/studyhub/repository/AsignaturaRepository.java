package com.studyhub.repository;

// Importar las clases necesarias
import com.studyhub.model.Asignatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * =====================================================================
 * REPOSITORIO JPA — Acceso a la tabla de asignaturas
 * =====================================================================
 * 
 * Un Repository es la capa que se comunica DIRECTAMENTE con la BD.
 * Es como un "mesero" entre el Controlador y la Base de Datos.
 * 
 * FLUJO:
 *   Front-end → Controller → Repository → Base de Datos H2
 * 
 * ¿POR QUÉ ES UNA INTERFAZ Y NO UNA CLASE?
 * Porque Spring Data JPA genera la implementación AUTOMÁTICAMENTE.
 * Tú solo declaras la interfaz, y Spring crea todo el código SQL por ti.
 * 
 * JpaRepository<Asignatura, Long> significa:
 * - Asignatura → El tipo de entidad que maneja (la tabla "asignaturas")
 * - Long       → El tipo de dato de la llave primaria (el ID)
 * 
 * MÉTODOS QUE OBTIENES GRATIS (sin escribir código):
 * ┌───────────────────────────┬─────────────────────────────────────────┐
 * │ Método                    │ SQL que genera automáticamente           │
 * ├───────────────────────────┼─────────────────────────────────────────┤
 * │ save(asignatura)          │ INSERT INTO asignaturas VALUES (...)    │
 * │ findAll()                 │ SELECT * FROM asignaturas              │
 * │ findById(id)              │ SELECT * FROM asignaturas WHERE id = ? │
 * │ deleteById(id)            │ DELETE FROM asignaturas WHERE id = ?   │
 * │ existsById(id)            │ SELECT COUNT(*) WHERE id = ?           │
 * │ count()                   │ SELECT COUNT(*) FROM asignaturas       │
 * └───────────────────────────┴─────────────────────────────────────────┘
 * 
 * ¡No necesitas escribir SQL ni implementar estos métodos!
 */
@Repository  // Le dice a Spring: "Esta interfaz es un componente de acceso a datos"
public interface AsignaturaRepository extends JpaRepository<Asignatura, Long> {
    
    // ¡Vacía! JpaRepository ya provee todos los métodos CRUD.
    // Si necesitaras queries personalizados, los agregarías aquí, por ejemplo:
    // List<Asignatura> findByPeriodo(String periodo);
    // List<Asignatura> findByProfesor(String profesor);
}
