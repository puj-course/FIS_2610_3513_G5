package com.studyhub.model;

// Importar las anotaciones JPA (Java Persistence API)
// JPA es el estándar de Java para conectar objetos con tablas de BD
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * =====================================================================
 * ENTIDAD JPA — Asignatura (Modelo de datos)
 * =====================================================================
 * 
 * Esta clase representa una ASIGNATURA en la base de datos.
 * Cada instancia de esta clase = una FILA en la tabla "asignaturas".
 * 
 * JPA se encarga de:
 * - Crear la tabla automáticamente en la BD (si no existe)
 * - Convertir objetos Java ↔ filas de la tabla
 * - Generar el SQL (INSERT, SELECT, UPDATE, DELETE) por ti
 * 
 * EJEMPLO:
 * ┌────┬──────────────────────┬─────────┬───────────┬───────────────┬──────────┬─────────┐
 * │ ID │ NOMBRE               │ CODIGO  │ PROFESOR  │ HORARIO       │ CREDITOS │ PERIODO │
 * ├────┼──────────────────────┼─────────┼───────────┼───────────────┼──────────┼─────────┤
 * │ 1  │ Cálculo Diferencial  │ MAT101  │ Dr. García│ Lunes 8-10    │ 4        │ 2026-1  │
 * │ 2  │ Fund. Ing. Software  │ FIS2610 │ Ing. López│ Martes 10-12  │ 3        │ 2026-1  │
 * └────┴──────────────────────┴─────────┴───────────┴───────────────┴──────────┴─────────┘
 */
@Entity  // Le dice a JPA: "Esta clase es una tabla en la base de datos"
@Table(name = "asignaturas")  // El nombre de la tabla en la BD será "asignaturas"
public class Asignatura {

    /**
     * LLAVE PRIMARIA — Identificador único de cada asignatura.
     * 
     * @Id → Le dice a JPA que este campo es la llave primaria (PK)
     * @GeneratedValue → El ID se genera automáticamente (1, 2, 3, ...)
     *                    IDENTITY = la base de datos asigna el número
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre completo de la asignatura (ej: "Cálculo Diferencial") */
    private String nombre;
    
    /** Código de la materia (ej: "MAT101", "FIS2610") */
    private String codigo;
    
    /** Nombre del profesor (ej: "Dr. García") */
    private String profesor;
    
    /** Horario de clase (ej: "Lunes 8:00-10:00") */
    private String horario;
    
    /** Número de créditos (ej: 3, 4) */
    private int creditos;
    
    /** Periodo académico (ej: "2026-1") */
    private String periodo;

    /**
     * CONSTRUCTOR VACÍO
     * JPA necesita un constructor sin parámetros para poder
     * crear objetos cuando lee datos de la base de datos.
     * También lo necesita Spring para deserializar el JSON
     * que llega desde el front-end.
     */
    public Asignatura() {
    }

    /**
     * CONSTRUCTOR CON PARÁMETROS
     * Para crear una asignatura nueva con todos sus datos.
     * No incluye 'id' porque la BD lo genera automáticamente.
     */
    public Asignatura(String nombre, String codigo, String profesor,
                      String horario, int creditos, String periodo) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.profesor = profesor;
        this.horario = horario;
        this.creditos = creditos;
        this.periodo = periodo;
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================
    // Estos métodos permiten leer y modificar los campos del objeto.
    // JPA y Spring los necesitan para acceder a los datos.
    // Sin ellos, el JSON no se convierte correctamente a/desde Java.

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getProfesor() { return profesor; }
    public void setProfesor(String profesor) { this.profesor = profesor; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public int getCreditos() { return creditos; }
    public void setCreditos(int creditos) { this.creditos = creditos; }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    /**
     * toString() — Para imprimir el objeto en consola de forma legible.
     * Se usa en los System.out.println del controlador.
     */
    @Override
    public String toString() {
        return "Asignatura{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", codigo='" + codigo + '\'' +
                ", profesor='" + profesor + '\'' +
                ", horario='" + horario + '\'' +
                ", creditos=" + creditos +
                ", periodo='" + periodo + '\'' +
                '}';
    }
}
