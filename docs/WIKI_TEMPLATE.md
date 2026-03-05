# Wiki del Proyecto StudyHub

Este documento contiene las secciones base de la Wiki para ser cargadas en GitHub.

## 1. Visión General del Proyecto
**Objetivo del sistema:** StudyHub es una aplicación orientada a mejorar la organización académica de los estudiantes mediante la centralización de sus datos críticos.
**Alcance:** Gestión de materias, horarios, notas y notificaciones de eventos académicos.
**Problema que se busca resolver:** La dispersión de información académica en múltiples formatos y plataformas.
**Stakeholders:** Estudiantes universitarios, personal administrativo (opcional).

## 2. Gestión de Requerimientos
### 2.1 Casos de Uso / Historias de Usuario
*   **Historias de Usuario:**
    *   Como estudiante, quiero ver mis materias registradas para saber mi carga académica.
    *   Como estudiante, quiero registrar mis notas para monitorear mi promedio.
    *   Como estudiante, quiero ver mi horario semanal para planear mi día.

## 3. Arquitectura y Diseño
**Estilo arquitectónico:** Monolito MVC (Model-View-Controller) con Spring Boot.
**Patrones de diseño utilizados:** DAO (Data Access Object), Factory, Singleton.
**Separación de capas:** 
1.  **Controller:** Manejo de peticiones HTTP.
2.  **Service:** Lógica de negocio.
3.  **Repository:** Acceso a datos.
4.  **Entity:** Representación de la BD.

## 4. Componentes y Tecnologías
### 4.1 Frontend
**Tecnología:** JavaFX / Thymeleaf / HTML
**Responsabilidades:** Interfaz de usuario y visualización de datos.
### 4.2 Backend
**Servicios:** API REST de gestión de materias.
**Lógica de negocio:** Cálculo de promedios, validaciones de fechas.
### 4.3 Base de Datos
**Motor de BD:** PostgreSQL
**Modelo de datos:** Relacional.

## 5. Diagramas del Sistema
*(A completar con diagramas de clase y EBC)*

## 6. Modelo de Inteligencia Artificial
*(Pendiente de definición según el proyecto)*

## 7. DevOps y Automatización
**Pipelines CI/CD:** GitHub Actions para compilación automática.
**Despliegue:** Docker para contenedorización.

## 8. Métricas de Calidad
Resultados de SonarQube y cobertura de pruebas unitarias.
