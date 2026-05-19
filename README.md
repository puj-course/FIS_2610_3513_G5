# StudyHub

## Descripción
StudyHub es una aplicación universitaria diseñada para la gestión integral de materias, notas y horarios. El objetivo es proporcionar a los estudiantes una herramienta centralizada para el seguimiento académico, facilitando la organización de sus responsabilidades y el monitoreo de su progreso en tiempo real.

---

## Equipo del Proyecto
| Nombre        | Rol                   | GitHub / Perfil         |
|--------------|-----------------------|--------------------------|
| Sarah        | Scrum Master          | github.com/barrerosarah9 |
| Valeria      | Product Owner / QA Lead | github.com/valgomezzz    |
| Manuel       | Sprint Planner        | github.com/Manuel-jmMo   |
| Matias       | Configuration Manager | github.com/matiasssss10  |
| Federico     | DevOps Engineer       | github.com/Fede-17       |

---

## Requisitos del Sistema

### Requisitos Funcionales
- RF1: Registro y gestión de materias académicas.  
- RF2: Visualización de calendario académico centralizado.  
- RF3: Cálculo automático de promedios y progreso académico.  
- RF4: Sistema de notificaciones de eventos y tareas.  
- RF5: Verificación de identidad por SMS (Twilio).  
- RF6: Gestión de cronogramas y exportación de horarios.  
- RF7: Sistema de reseñas de asignaturas.  
- RF8: Gestión de apuntes por materia.  

### Requisitos No Funcionales
- Usabilidad: Interfaz clara, intuitiva y accesible.  
- Rendimiento: Respuesta eficiente ante consultas frecuentes.  
- Seguridad: Protección de datos personales del usuario (BCrypt, JWT).  
- Disponibilidad: Despliegue continuo en Render con healthcheck.  

---

## Arquitectura y Diseño
El sistema se concibe bajo una arquitectura cliente-servidor (Monolito MVC), donde una aplicación cliente consume servicios a través de una API REST central.

### Patrones de Diseño Implementados
| Patrón | Ubicación | Propósito |
|--------|-----------|-----------|
| **Builder** | `service/builder/` | Construcción de objetos `Horario` y `ResumenAcademico` |
| **Command** | `service/command/` | Operaciones sobre notificaciones (eliminar, marcar leída) |
| **Decorator** | `service/decorator/` | Extensión dinámica de tareas (próxima, vencida) |
| **Facade** | `service/facade/` | Simplificación de autenticación y horarios |
| **Factory** | `service/factory/` | Creación de distintos tipos de notificaciones |
| **Observer** | `service/observer/` | Publicación de eventos de notas y alertas académicas |
| **Strategy** | `service/strategy/` | Encriptación de contraseñas y estados de tareas |
| **Proxy** | `UsuarioServiceProxy` | Control de acceso al servicio de usuarios |

---

## Tecnologías Utilizadas
- **Frontend:** HTML / CSS / JavaScript (SPA)
- **Backend:** Java 17 – Spring Boot (API REST)
- **Base de Datos:** PostgreSQL (Supabase en producción)
- **Servidor Web:** Nginx (proxy reverso para el frontend)
- **Autenticación:** JWT + BCrypt
- **SMS:** Twilio Verify
- **DevOps:** GitHub Actions, Docker, Docker Compose
- **Calidad:** JaCoCo, SonarQube
- **Control de versiones:** Git + GitHub
- **Despliegue:** Render (producción), Docker Hub (imágenes)

---

## Estructura del Proyecto
```text
FIS_2610_3513_G5/
├── .github/
│   └── workflows/                    # Pipelines CI/CD
│       ├── ci-pruebas.yml            # Pipeline principal de pruebas y calidad
│       ├── cd_render.yml             # Despliegue a Render
│       ├── docker-publish.yml        # Publicación de imágenes en Docker Hub
│       ├── docker-compose-deploy.yml # Despliegue con Docker Compose
│       ├── SonarQube.yml             # Análisis de calidad con SonarQube
│       ├── CD.yml                    # Entrega continua
│       ├── ai_user_stories.yml       # Generación de historias de usuario con IA
│       ├── issue_to_done.yml         # Automatización de issues
│       └── sprint-report.yml         # Reporte automático de sprint
├── conf/                             # Archivos de configuración
│   ├── SecurityConfig.java
│   ├── config.yaml
│   └── settings.json
├── docs/                             # Documentación del proyecto
│   ├── Diagramas/                    # Diagramas UML y de arquitectura
│   ├── Exportación de Horario/       # Documentación de exportación
│   ├── api/                          # Documentación de la API
│   ├── architecture/                 # Documentación de arquitectura
│   ├── user_guide/                   # Guía de usuario
│   ├── estructura-horario-semanal.md
│   └── fundamentos.pdf
├── jupyter/                          # Análisis de datos
│   ├── datasets/                     # Datos para análisis
│   └── notebooks/                    # Notebooks de Jupyter
├── scripts/                          # Scripts de utilidad
│   ├── deploy.sh                     # Script de despliegue
│   ├── generate_hu.py                # Generador de historias de usuario
│   ├── setup.sh                      # Script de configuración inicial
│   └── test.sh                       # Script de ejecución de pruebas
├── src/
│   ├── Back-end/                     # API REST (Spring Boot)
│   │   ├── pom.xml                   # Configuración Maven y dependencias
│   │   └── src/
│   │       ├── main/java/com/studyhub/
│   │       │   ├── StudyHubApplication.java
│   │       │   ├── SecurityConfig.java
│   │       │   ├── WebConfig.java
│   │       │   ├── controller/       # Controladores REST
│   │       │   │   ├── ApunteController.java
│   │       │   │   ├── AsignaturaController.java
│   │       │   │   ├── AuthController.java
│   │       │   │   ├── CronogramaController.java
│   │       │   │   ├── ExportController.java
│   │       │   │   ├── ImagenPerfilController.java
│   │       │   │   ├── NotaController.java
│   │       │   │   ├── NotificationController.java
│   │       │   │   ├── ResenaController.java
│   │       │   │   ├── ScheduleController.java
│   │       │   │   ├── TareaController.java
│   │       │   │   └── UsuarioController.java
│   │       │   ├── dto/              # Objetos de transferencia de datos
│   │       │   │   ├── AsignaturaResumenDTO.java
│   │       │   │   ├── AuthResponse.java
│   │       │   │   ├── EstadisticasDTO.java
│   │       │   │   ├── LoginRequest.java
│   │       │   │   ├── RecuperarRequest.java
│   │       │   │   ├── RegistroRequest.java
│   │       │   │   ├── RestablecerRequest.java
│   │       │   │   ├── ResumenAcademicoDTO.java
│   │       │   │   ├── SubjectSummaryDTO.java
│   │       │   │   ├── TareaResumenDTO.java
│   │       │   │   ├── UsuarioResumenDTO.java
│   │       │   │   └── horarioDTO.java
│   │       │   ├── model/            # Entidades JPA
│   │       │   │   ├── Apunte.java
│   │       │   │   ├── Asignacion.java
│   │       │   │   ├── Asignatura.java
│   │       │   │   ├── Cronograma.java
│   │       │   │   ├── Nota.java
│   │       │   │   ├── Notification.java
│   │       │   │   ├── PasswordResetToken.java
│   │       │   │   ├── Resena.java
│   │       │   │   ├── SesionInvalidada.java
│   │       │   │   ├── Tarea.java
│   │       │   │   ├── Turno.java
│   │       │   │   ├── Usuario.java
│   │       │   │   └── UsuarioBuilder.java
│   │       │   ├── repository/       # Repositorios JPA
│   │       │   │   ├── ApunteRepository.java
│   │       │   │   ├── AsignacionRepository.java
│   │       │   │   ├── AsignaturaRepository.java
│   │       │   │   ├── CronogramaRepository.java
│   │       │   │   ├── HorarioRepository.java
│   │       │   │   ├── NotaRepository.java
│   │       │   │   ├── NotificationRepository.java
│   │       │   │   ├── PasswordResetTokenRepository.java
│   │       │   │   ├── ResenaRepository.java
│   │       │   │   ├── SesionInvalidadaRepository.java
│   │       │   │   ├── TareaRepository.java
│   │       │   │   ├── TurnoRepository.java
│   │       │   │   └── UsuarioRepository.java
│   │       │   └── service/          # Lógica de negocio
│   │       │       ├── ApunteService.java
│   │       │       ├── AsignaturaService.java
│   │       │       ├── AuthService.java
│   │       │       ├── CronogramaService.java
│   │       │       ├── EmailService.java
│   │       │       ├── EstadoTareaService.java
│   │       │       ├── ExportService.java
│   │       │       ├── IUsuarioService.java
│   │       │       ├── NotaService.java
│   │       │       ├── NotificationService.java
│   │       │       ├── ResenaService.java
│   │       │       ├── SmsService.java
│   │       │       ├── TareaNotificacionService.java
│   │       │       ├── UsuarioService.java
│   │       │       ├── UsuarioServiceProxy.java
│   │       │       ├── builder/      # Patrón Builder
│   │       │       │   ├── HorarioBuilder.java
│   │       │       │   └── ResumenAcademicoBuilder.java
│   │       │       ├── command/      # Patrón Command
│   │       │       │   ├── EliminarNotificacionCommand.java
│   │       │       │   ├── MarcarLeidaCommand.java
│   │       │       │   ├── NotificacionCommand.java
│   │       │       │   └── NotificacionCommandInvoker.java
│   │       │       ├── decorator/    # Patrón Decorator
│   │       │       │   ├── TareaBase.java
│   │       │       │   ├── TareaDecorator.java
│   │       │       │   ├── TareaProximaDecorator.java
│   │       │       │   ├── TareaSimple.java
│   │       │       │   └── TareaVencidaDecorator.java
│   │       │       ├── facade/       # Patrón Facade
│   │       │       │   ├── AuthFacade.java
│   │       │       │   └── horarioFacade.java
│   │       │       ├── factory/      # Patrón Factory
│   │       │       │   ├── NotificacionClaseProximaFactory.java
│   │       │       │   ├── NotificacionFactory.java
│   │       │       │   ├── NotificacionMateriaEnRiesgoFactory.java
│   │       │       │   ├── NotificacionSistemaFactory.java
│   │       │       │   └── NotificacionTareaPendienteFactory.java
│   │       │       ├── observer/     # Patrón Observer
│   │       │       │   ├── NotaEventPublisher.java
│   │       │       │   ├── NotaObserver.java
│   │       │       │   └── SistemaAlertasObserver.java
│   │       │       └── strategy/     # Patrón Strategy
│   │       │           ├── BCryptEncryptionStrategy.java
│   │       │           ├── EstadoPendienteStrategy.java
│   │       │           ├── EstadoProximaStrategy.java
│   │       │           ├── EstadoStrategy.java
│   │       │           ├── EstadoVencidaStrategy.java
│   │       │           └── PasswordEncryptionStrategy.java
│   │       └── test/java/com/studyhub/service/  # Pruebas unitarias
│   │           ├── CronogramaServiceTest.java
│   │           ├── EstadoTareaServiceTest.java
│   │           ├── ExportServiceTest.java
│   │           ├── HorarioServiceTest.java
│   │           ├── NotaServiceTest.java
│   │           ├── NotificacionFactoryTest.java
│   │           ├── NotificationCommandTest.java
│   │           ├── NotificationServiceTest.java
│   │           ├── ObserverSistemaAlertasTest.java
│   │           ├── ResenaServiceTest.java
│   │           ├── ResumenAcademicoBuilderTest.java
│   │           ├── TestResultLogger.java
│   │           ├── UsuarioServiceProxyTest.java
│   │           └── UsuarioServiceTest.java
│   └── Front-End/                    # Interfaz de usuario (SPA)
│       ├── index.html                # Aplicación principal
│       ├── css/
│       │   └── cronograma.css
│       └── js/
│           ├── auth.js               # Autenticación
│           ├── configuracion.js      # Configuración del usuario
│           ├── cronograma.js         # Gestión de cronogramas
│           ├── dashboard.js          # Panel principal
│           ├── exportHorario.js      # Exportación de horarios
│           ├── horario.js            # Gestión de horarios
│           ├── horarioObserver.js    # Observer de horarios
│           └── notification.js       # Sistema de notificaciones
├── .gitignore
├── CHANGELOG.md                      # Registro de cambios del proyecto
├── CONTRIBUTING.md                   # Guía para contribuir al repositorio
├── Dockerfile                        # Dockerfile del backend (multi-stage)
├── Dockerfile_Front                  # Dockerfile del frontend (Nginx)
├── LICENSE                           # Licencia del proyecto
├── Makefile                          # Comandos de automatización
├── RETROSPECTIVA_SPRINT.md           # Retrospectiva de sprints
├── docker-compose.yml                # Orquestación de servicios Docker
├── nginx.conf                        # Configuración de Nginx (proxy reverso)
├── render.yaml                       # Configuración de despliegue en Render
└── README.md                         # Este archivo
```

---

## Docker y Contenerización

### Arquitectura de Contenedores

El proyecto utiliza **Docker Compose** para orquestar tres servicios que se comunican a través de una **red Docker personalizada** (`studyhub-network`):

```
┌─────────────────────────────────────────────────────────┐
│                  studyhub-network (bridge)               │
│                                                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   frontend    │  │   backend    │  │      db      │  │
│  │  (Nginx)      │──│ (Spring Boot)│──│ (PostgreSQL) │  │
│  │  Puerto: 80   │  │ Puerto: 8080 │  │ Puerto: 5432 │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

### Dockerfiles

| Archivo | Servicio | Descripción |
|---------|----------|-------------|
| `Dockerfile` | Backend | Multi-stage build: Maven 3.9 + JDK 17 (compilación) → JRE 17 (ejecución). Incluye el frontend como recursos estáticos. |
| `Dockerfile_Front` | Frontend | Nginx Alpine sirviendo archivos estáticos con proxy reverso al backend. |

### Red Docker (`studyhub-network`)

Los contenedores se comunican internamente por **nombre de servicio** gracias a la red `studyhub-network` con driver `bridge`:

- El frontend llama al backend como `http://backend:8080` (configurado en `nginx.conf`)
- El backend puede conectarse a la base de datos como `jdbc:postgresql://db:5432/studyhub`
- Los puertos (`80`, `8080`, `5432`) solo se exponen al host para acceso externo

### Nginx como Proxy Reverso

El archivo `nginx.conf` configura Nginx para:
- Servir los archivos estáticos del frontend en `/`
- Redirigir las llamadas `/api/*` al backend (`http://backend:8080/`)

---

## Instalación y Ejecución

### Requisitos Previos
- Docker y Docker Compose
- Git
- Java 17 y Maven (si se ejecuta sin Docker)

### Clonar el repositorio
```bash
git clone https://github.com/puj-course/FIS_2610_3513_G5.git
cd FIS_2610_3513_G5
```

### Ejecución con Docker Compose

Para levantar todos los servicios:
```bash
docker-compose up --build
```

Para ejecutarlos en segundo plano:
```bash
docker-compose up --build -d
```

Para detener los servicios:
```bash
docker-compose down
```

Para verificar que los servicios están corriendo:
```bash
docker-compose ps
```

El frontend estará disponible en `http://localhost:80` y el backend en `http://localhost:8080`.

### Ejecución local (sin Docker)

```bash
cd src/Back-end
mvnw spring-boot:run
```

### Ejecución de pruebas unitarias

```bash
cd src/Back-end
mvnw test
```

### Variables de Entorno

El backend requiere las siguientes variables de entorno (configuradas en `docker-compose.yml`):

| Variable | Descripción |
|---|---|
| `JDBC_DATABASE_URL` | URL de conexión a la base de datos PostgreSQL |
| `JDBC_DATABASE_USERNAME` | Usuario de la base de datos |
| `JDBC_DATABASE_PASSWORD` | Contraseña de la base de datos |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Estrategia de inicialización del esquema |
| `PORT` | Puerto en el que corre el backend |
| `TWILIO_ACCOUNT_SID` | SID de la cuenta Twilio (verificación SMS) |
| `TWILIO_AUTH_TOKEN` | Token de autenticación Twilio |
| `TWILIO_VERIFY_SID` | SID del servicio Twilio Verify |

---

## CI/CD y Calidad

### Pipelines de GitHub Actions

| Workflow | Archivo | Descripción |
|----------|---------|-------------|
| Pruebas y Calidad | `ci-pruebas.yml` | Ejecuta pruebas unitarias, JaCoCo y genera dashboard de calidad |
| SonarQube | `SonarQube.yml` | Análisis estático de código |
| Docker Publish | `docker-publish.yml` | Construye y publica imágenes en Docker Hub |
| Deploy Render | `cd_render.yml` | Despliegue automático en Render |
| Sprint Report | `sprint-report.yml` | Generación automática de reportes de sprint |
| AI User Stories | `ai_user_stories.yml` | Generación de historias de usuario con IA |

### Despliegue en Producción

El despliegue en producción se realiza automáticamente en Render mediante GitHub Actions al hacer push a la rama `main`. El pipeline ejecuta:

1. Construcción y publicación de imágenes Docker en Docker Hub (con tags `:latest` y `:sha`)
2. Despliegue automático en Render vía deploy hook
3. Healthcheck del servicio desplegado
4. Notificación del resultado por Telegram

---

## Pruebas

El proyecto cuenta con **14 archivos de pruebas unitarias** que cubren los servicios principales y los patrones de diseño:

| Test | Cobertura |
|------|-----------|
| `UsuarioServiceTest` | Registro, login, gestión de perfil |
| `NotaServiceTest` | Cálculo de notas y promedios |
| `NotificationServiceTest` | Sistema de notificaciones |
| `NotificationCommandTest` | Patrón Command en notificaciones |
| `NotificacionFactoryTest` | Patrón Factory en notificaciones |
| `ObserverSistemaAlertasTest` | Patrón Observer en alertas |
| `ResumenAcademicoBuilderTest` | Patrón Builder |
| `UsuarioServiceProxyTest` | Patrón Proxy |
| `CronogramaServiceTest` | Gestión de cronogramas |
| `EstadoTareaServiceTest` | Estados de tareas (Strategy) |
| `ExportServiceTest` | Exportación de horarios |
| `HorarioServiceTest` | Gestión de horarios |
| `ResenaServiceTest` | Sistema de reseñas |

---

## Contexto Académico
- **Asignatura:** Fundamentos de Ingeniería de Software
- **Docente:** Luis Gabriel Moreno Sandoval, PhD
- **Institución:** Pontificia Universidad Javeriana

---

## Equipo de Desarrollo y Contacto

**Valeria Gómez**  
Estudiante de Ingeniería de Sistemas, Pontificia Universidad Javeriana  
📧 [valeria.gomezb@javeriana.edu.co](mailto:valeria.gomezb@javeriana.edu.co)

**Federico Mejía**  
Estudiante de Ingeniería de Sistemas, Pontificia Universidad Javeriana  
📧 [federico_mejia@javeriana.edu.co](mailto:federico_mejia@javeriana.edu.co)

**Matias Mendoza**  
Estudiante de Ingeniería de Sistemas, Pontificia Universidad Javeriana  
📧 [Danielmmendoza@javeriana.edu.co](mailto:Danielmmendoza@javeriana.edu.co)

**Sarah Barrero**  
Estudiante de Ingeniería de Sistemas, Pontificia Universidad Javeriana  
📧 [slbarrero@javeriana.edu.co](mailto:slbarrero@javeriana.edu.co)

**Manuel Morales**  
Estudiante de Ingeniería de Sistemas, Pontificia Universidad Javeriana
📧 [jm-movilla@javeriana.edu.co](mailto:jm-movilla@javeriana.edu.co)

---

## Licencia
Proyecto desarrollado con fines académicos.
