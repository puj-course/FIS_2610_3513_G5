# StudyHub

## Descripción
StudyHub es una aplicación universitaria diseñada para la gestión integral de materias, notas y horarios. El objetivo es proporcionar a los estudiantes una herramienta centralizada para el seguimiento académico, facilitando la organización de sus responsabilidades y el monitoreo de su progreso en tiempo real.

---

## Equipo del Proyecto
| Nombre        | Rol                   | GitHub / Perfil         |
|--------------|-----------------------|--------------------------|
| Sarah        | Scrum Master          | github.com/barrerosarah9 |
| Valeria      | Product Owner         | github.com/valgomezzz    |
| Manuel       | Sprint Planner        | github.com/Manuel-jmMo   |
| Matias       | Configuration Manager | github.com/matiasssss10  |
| Valeria      | QA Lead               | github.com/valgomezzz    |
| Federico     | DevOps Engineer       | github.com/Fede-17       |

---

## Requisitos del Sistema

### Requisitos Funcionales
- RF1: Registro y gestión de materias académicas.  
- RF2: Visualización de calendario académico centralizado.  
- RF3: Cálculo automático de promedios y progreso académico.  
- RF4: Sistema de notificaciones de eventos y tareas.  

### Requisitos No Funcionales
- Usabilidad: Interfaz clara, intuitiva y accesible.  
- Rendimiento: Respuesta eficiente ante consultas frecuentes.  
- Seguridad: Protección de datos personales del usuario.  

---

## Arquitectura y Diseño
El sistema se concibe bajo una arquitectura cliente-servidor (Monolito MVC), donde una aplicación cliente consume servicios a través de una API central.

---

## Tecnologías Utilizadas
- **Frontend:** JavaFX / HTML / CSS
- **Backend:** Java – Spring Boot -> Actualizado a Firebase
- **Base de Datos:** MySQL -> Actualizado a Firebase
- **IA / Data Science:** Python, Pandas (Futuro)
- **DevOps:** GitHub Actions, Docker
- **Control de versiones:** Git

---

## Estructura del Proyecto
```text
FIS_2610_3513_G5/
├── .github/                # Configuración de GitHub (Workflows e Issue Templates)
├── conf/                   # Archivos de configuración general
├── docs/                   # Documentación, API y guías de usuario
├── jupyter/                # Notebooks de Jupyter y datasets de análisis
├── scripts/                # Scripts de utilidad (setup, deploy, test)
├── src/                    # Código fuente del proyecto
│   ├── Back-end/           # Servidor API REST (Spring Boot)
│   │   ├── src/            # Código fuente Java (MVC)
│   │   ├── target/         # Archivos compilados
│   │   └── pom.xml         # Configuración de Maven y dependencias
│   └── Front-End/          # Interfaz de usuario (Cliente)
│       ├── index.html      # Aplicación principal (SPA)
│       └── js/             # Lógica de JavaScript (Observers/Servicios)
├── .gitignore              # Archivos ignorados por Git
├── CHANGELOG.md            # Registro de cambios del proyecto
├── CONTRIBUTING.md         # Guía para contribuir al repositorio
├── Dockerfile              # Configuración de Docker
├── docker-compose.yml      # Configuración de Docker Compose
├── LICENSE                 # Licencia del proyecto
├── Makefile                # Comandos de automatización
└── README.md               # Este archivo
```

---

## Instalación y Ejecución

### Requisitos
- Docker y Docker Compose
- Git

### Clonar el repositorio
```bash
git clone https://github.com/puj-course/FIS_2610_3513_G5.git
cd FIS_2610_3513_G5
```

### Ejecución con Docker Compose

El proyecto incluye tres servicios orquestados mediante Docker Compose:
- **db**: Base de datos PostgreSQL local
- **backend**: API REST en Spring Boot (puerto 8080)
- **frontend**: Interfaz de usuario servida por Nginx (puerto 80)

Los servicios se comunican a través de la red interna `studyhub-network`.

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

### Variables de entorno

El backend requiere las siguientes variables de entorno, configuradas en el `docker-compose.yml`:

| Variable | Descripción |
|---|---|
| `JDBC_DATABASE_URL` | URL de conexión a la base de datos PostgreSQL |
| `JDBC_DATABASE_USERNAME` | Usuario de la base de datos |
| `JDBC_DATABASE_PASSWORD` | Contraseña de la base de datos |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Estrategia de inicialización del esquema |
| `PORT` | Puerto en el que corre el backend |

### Verificar que los servicios están corriendo

```bash
docker-compose ps
```

El backend estará disponible en `http://localhost:8080` y el frontend en `http://localhost:80`.

### Despliegue en producción

El despliegue en producción se realiza automáticamente en Render mediante GitHub Actions al hacer push a la rama `main`. El pipeline ejecuta las siguientes etapas:

1. Construcción y publicación de imágenes Docker en Docker Hub (con tags `:latest` y `:sha` del commit)
2. Despliegue automático en Render vía deploy hook
3. Healthcheck del servicio desplegado
4. Notificación del resultado por Telegram

## Clonar el repositorio
```text
git clone https://github.com/puj-course/FIS_2610_3513_G5.git
cd FIS_2610_3513_G5
```

## Ejecución con Docker
No se ha llegado a la implementación con Docker

## Contexto Académico
- **Asignatura:** Fundamentos de Ingeniería de Software
- **Docente:** Luis Gabriel Moreno Sandoval, PhD
- **Institución:** Pontificia Universidad Javeriana

---

## Equipo de desarrollo y Contacto
**Valeria Gómez**  
Estudiante de Ingeniería de Sistemas, Pontificia Universidad Javeriana  
📧[valeria.gomezb@javeriana.edu.co](mailto:valeria.gomezb@javeriana.edu.co)

**Federico Mejía**
Estudiante de Ingeniería en Sistemas, Pontificia Universidad Javeriana  
📧[federico_mejia@javeriana.edu.co](mailto:federico_mejia@javeriana.edu.co)

**Matias Mendoza**
Estudiante de Ingeniería en Sistemas, Pontificia Universidad Javeriana  
📧[Danielmmendoza@javeriana.edu.co](mailto:Danielmmendoza@javeriana.edu.co)

**Sarah Barrero**
Estudiante de Ingeniería en Sistemas, Pontificia Universidad Javeriana  
📧 [slbarrero@javeriana.edu.co](mailto:slbarrero@javeriana.edu.co)

---

## Licencia
Proyecto desarrollado con fines académicos.
