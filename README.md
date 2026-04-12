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
**Requisitos**
- Docker y Docker Compose
- Git
- Java 17+
- Maven

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
