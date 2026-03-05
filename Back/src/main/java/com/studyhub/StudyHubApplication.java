package com.studyhub;

// Importar las clases necesarias de Spring Boot
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * =====================================================================
 * CLASE PRINCIPAL — El "motor" de la aplicación
 * =====================================================================
 * 
 * Esta es la clase que arranca todo el servidor Spring Boot.
 * 
 * @SpringBootApplication hace 3 cosas automáticamente:
 * 1. @Configuration    → Permite configurar beans (objetos compartidos)
 * 2. @ComponentScan    → Busca automáticamente todas las clases con
 *                        @Controller, @Service, @Repository, etc.
 *                        dentro de este paquete y sub-paquetes
 * 3. @EnableAutoConfiguration → Configura automáticamente las librerías
 *                               (H2, JPA, Tomcat, etc.)
 * 
 * Cuando ejecutas esta clase, Spring Boot:
 * - Inicia un servidor Tomcat en el puerto 8080
 * - Escanea todos los controladores y los registra
 * - Se conecta a la base de datos H2
 * - Crea las tablas automáticamente según las entidades (@Entity)
 * 
 * Comando para ejecutar: mvn spring-boot:run
 */
@SpringBootApplication
public class StudyHubApplication {
    
    /**
     * Método main — Punto de entrada de la aplicación.
     * SpringApplication.run() inicia todo el contexto de Spring Boot.
     * 
     * @param args Argumentos de línea de comandos (normalmente vacío)
     */
    public static void main(String[] args) {
        SpringApplication.run(StudyHubApplication.class, args);
    }
}
