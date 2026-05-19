# CHANGELOG — StudyHub (FIS_2610_3513_G5)

> Este documento registra cronológicamente las funcionalidades y mejoras añadidas al proyecto a lo largo de cada sprint.
> Dado que el proyecto no utilizó releases formales de GitHub, la trazabilidad de cambios se realiza a través de los entregables de cada sprint, vinculando cada funcionalidad a sus commits correspondientes.

---

## Sprint 1 — Feb 13

> Sprint de arranque del proyecto. Definición del alcance, distribución de roles y configuración del entorno de desarrollo.

---

## Sprint 2 — Feb 16 – Feb 22

> Configuración técnica inicial, primeras integraciones front-back y definición de entidades base.

---

## Sprint 3 — Feb 23 – Mar 01

> Configuración de infraestructura base del proyecto, setup del repositorio y estructura inicial del back-end y front-end.

---

## Sprint 4 — Mar 02 – Mar 08

### Funcionalidades implementadas
- **Envío de materias al back-end** — Equipo
  - Entidad Asignatura ([6fade2c](https://github.com/puj-course/FIS_2610_3513_G5/commit/6fade2ce4f56a6bab783a0df46474b8f4395ed3d))
  - Controlador de Asignatura ([2258f44](https://github.com/puj-course/FIS_2610_3513_G5/commit/2258f4447c113491549c0ea81873e7f74043dd9f))

- **Diseño funcional & configuración técnica inicial** — Equipo

---

## Sprint 5 — Mar 15 – Mar 21

### Funcionalidades implementadas
- **Crear y ver una tarea** — Valeria Gómez, Manuel Movilla
  - Vista Ver tareas pendientes ([4ce2f5e](https://github.com/puj-course/FIS_2610_3513_G5/commit/4ce2f5e632d820c67039079f432f3c88aed04653))
  - Clase Tarea en el back-end ([8bc339b](https://github.com/puj-course/FIS_2610_3513_G5/commit/8bc339be1984b65d1a735a230495324a36260af4))
  - Repositorio y controlador para Tarea ([33b1802](https://github.com/puj-course/FIS_2610_3513_G5/commit/33b1802d15ca0f7762a4f246b28475781fa6e874))
  - Envío de datos al back-end ([fc41e45](https://github.com/puj-course/FIS_2610_3513_G5/commit/fc41e4592b21c8fb7cd3da9a505f281eef40ef30))

- **Entidad Nota y complementos en el back-end** — Federico Mejía
  - Entidad Nota, CalificaciónService, NotaRepository y NotaController ([4c51e1c](https://github.com/puj-course/FIS_2610_3513_G5/commit/4c51e1ccb120ef03f342c94356fe76631315a79e))

- **Añadir y ver calificaciones de una asignatura** — Sarah Barrero
  - Formulario para ingresar calificación ([ad72e57](https://github.com/puj-course/FIS_2610_3513_G5/commit/ad72e5743b3e793288f734c21a7960a7b6c499e7))
  - Opción Calificaciones en el menú de asignatura ([8617148](https://github.com/puj-course/FIS_2610_3513_G5/commit/86171489659ab828b171549026951916bc39b15b))

- **Calcular promedio de calificaciones por asignatura** — Manuel Movilla
  - Lógica para calcular promedio ([554807d](https://github.com/puj-course/FIS_2610_3513_G5/commit/554807d46f4ea72d7a767ab8524359d4837b1c88))
  - Integración del promedio con asignaturas ([19a0e6c](https://github.com/puj-course/FIS_2610_3513_G5/commit/19a0e6c7f6701d26b164a4de5770d72bb9630e55))

- **Editar una calificación en la base de datos** — Matías Mendoza
  - Método de actualización en CalificacionService ([68ccf78](https://github.com/puj-course/FIS_2610_3513_G5/commit/68ccf78935bfa1d95f20097391c2e468faf110fa))
  - Pruebas unitarias para la edición ([48658b3](https://github.com/puj-course/FIS_2610_3513_G5/commit/48658b36be21d35d378680c83b85b824f4178426))

---

## Sprint 6 — Mar 25 – Apr 04

### Funcionalidades implementadas
- **Entidad Usuario y su controlador en la base de datos** — Valeria Gómez
  - Entidad Usuario, UsuarioService, UsuarioRepository y UsuarioController ([15c614a](https://github.com/puj-course/FIS_2610_3513_G5/commit/15c614afc2221c850e0d3d256bb762011caa5e64))

- **Vistas de inicio de sesión y registro** — Valeria Gómez
  - Implementación de vistas de login y registro ([07e3d3d](https://github.com/puj-course/FIS_2610_3513_G5/commit/07e3d3d87fb33cff2be7d10363f9c0ecef1d2ae4))
  - Restricción de acceso al dashboard ([07e3d3d](https://github.com/puj-course/FIS_2610_3513_G5/commit/07e3d3d87fb33cff2be7d10363f9c0ecef1d2ae4))

- **Botones de acceso directo en el panel lateral** — Sarah Barrero
  - Vinculación de botones de Asignaturas y Tareas ([9776d06](https://github.com/puj-course/FIS_2610_3513_G5/commit/9776d06609755c3ec0d6972359e9dbbcb4a2141a))
  - Vistas de lista de Asignaturas y Tareas ([ef90750](https://github.com/puj-course/FIS_2610_3513_G5/commit/ef90750ebc9f90c3b15daa1f160b687cb367f743))

- **Vista de calendario interactivo con integración de tareas** — Manuel Movilla
  - Expansión y contracción del calendario ([4bc769c](https://github.com/puj-course/FIS_2610_3513_G5/commit/4bc769c9034b0ce11c751bcdaf881a62e228a036))
  - Endpoint para consultar tareas por fecha ([acfa5ef](https://github.com/puj-course/FIS_2610_3513_G5/commit/acfa5efdc1f3657130edaddddc86e52a65259345))
  - Integración del calendario con el back-end ([2bd0835](https://github.com/puj-course/FIS_2610_3513_G5/commit/2bd08352cb2fbcdb074589204ec7838e735f157d))

- **Visualización de asignaturas en el horario semanal** — Matías Mendoza
  - Ajuste de campos de día y horario en la entidad Asignatura ([768927d](https://github.com/puj-course/FIS_2610_3513_G5/commit/768927d379948a4c1861f544d70c133dd1d4e3fa))
  - Integración del horario semanal con el back-end ([421fc68](https://github.com/puj-course/FIS_2610_3513_G5/commit/421fc68158b6ba93fec40d54e62a6fe1b4ba9065))

---

## Sprint 7 — Apr 05 – Apr 11

### Funcionalidades implementadas
- **Autenticación de usuarios mediante login** — Federico Mejía
  - Endpoint de login ([b2b9f59](https://github.com/puj-course/FIS_2610_3513_G5/commit/b2b9f596d5ba32a0e0a74d38e2285d18b89da37b))
  - Conexión del formulario al back-end ([8388b9f](https://github.com/puj-course/FIS_2610_3513_G5/commit/8388b9f339ac0b1a29269f69f9be17de375e9b12))
  - Registro de nuevo usuario ([d78ec60](https://github.com/puj-course/FIS_2610_3513_G5/commit/d78ec60a93c89c0d7fc5c9885664a4fef918d560))

- **Calculadora de nota mínima para aprobar** — Sarah Barrero
  - Vista de calculadora y lógica de cálculo ([381588f](https://github.com/puj-course/FIS_2610_3513_G5/commit/381588f3c4e7376ce69088f47fe826ffe7404c17))
  - Endpoint para obtener calificaciones por asignatura ([1e3d46c](https://github.com/puj-course/FIS_2610_3513_G5/commit/1e3d46c5d6c7031da2103f40c31eeb4f41683ca2))

- **Persistencia de calificaciones en base de datos** — Manuel Movilla
  - Persistencia al registrar, editar y eliminar calificaciones ([7aa70c5](https://github.com/puj-course/FIS_2610_3513_G5/commit/7aa70c5e0a8aad58318a81d8ee203b47a20e102f))

- **Visualización de información del usuario en la barra lateral** — Valeria Gómez
  - Endpoint de resumen del usuario ([785a90f](https://github.com/puj-course/FIS_2610_3513_G5/commit/785a90fee336b147e11c2d6dbd4e9ee9c0144aea))
  - Actualización dinámica de materias y promedio global ([785a90f](https://github.com/puj-course/FIS_2610_3513_G5/commit/785a90fee336b147e11c2d6dbd4e9ee9c0144aea))

- **Creación, visualización y edición de apuntes rápidos** — Federico Mejía
  - Entidad Apunte y endpoints CRUD ([f8384f0](https://github.com/puj-course/FIS_2610_3513_G5/commit/f8384f02322c8e26d506b8fa742c37787f7f715c))
  - Pop-up de nueva nota y vista Mis notas ([f8384f0](https://github.com/puj-course/FIS_2610_3513_G5/commit/f8384f02322c8e26d506b8fa742c37787f7f715c))

- **Maquetación del foro de reseñas** — Matías Mendoza
  - Formulario de nueva reseña y lista con datos simulados ([b1490c2](https://github.com/puj-course/FIS_2610_3513_G5/commit/b1490c236021aaa289387cd8a7047bad5504eeb0))
  - Campo de búsqueda de profesores ([b1490c2](https://github.com/puj-course/FIS_2610_3513_G5/commit/b1490c236021aaa289387cd8a7047bad5504eeb0))

- **Visualización de materias en el horario semanal según días y horas** — Matías Mendoza
  - Ajuste de campos de día y horario en la entidad Asignatura ([768927d](https://github.com/puj-course/FIS_2610_3513_G5/commit/768927d379948a4c1861f544d70c133dd1d4e3fa))
  - Integración del horario semanal con el back-end ([421fc68](https://github.com/puj-course/FIS_2610_3513_G5/commit/421fc68158b6ba93fec40d54e62a6fe1b4ba9065))

---

## Sprint 8 — Apr 12 – Apr 18

### Funcionalidades implementadas
- **Gestión del horario académico del estudiante** — Matías Mendoza, Manuel Movilla
  - HorarioBuilder y HorarioFacade ([c7b496f](https://github.com/puj-course/FIS_2610_3513_G5/commit/c7b496ff7773685e412f8ce000a03996c4b9c5a2))
  - Patrón Observer para actualización dinámica del horario ([0828223](https://github.com/puj-course/FIS_2610_3513_G5/pull/280/commits/08282232c4149951d6e16a6b9be922cc1da3c6d3))
  - Diagrama UML de patrones Builder, Facade y Observer ([48c008d](https://github.com/puj-course/FIS_2610_3513_G5/pull/281/commits/48c008dee35d548f45f82ea2abc4a082f0a8b5ba))

- **Notificaciones inteligentes de tareas** — Manuel Movilla
  - Interfaz EstadoStrategy y clases concretas ([84ce43b](https://github.com/puj-course/FIS_2610_3513_G5/pull/282/commits/84ce43b994f190ba8392227d78204cf56e2ca815))
  - Patrón Decorator para estados de tarea ([7636c83](https://github.com/puj-course/FIS_2610_3513_G5/pull/284/commits/7636c831b65868ce1a70a267c389393ae74b205c))
  - Diagrama UML de patrones Strategy y Decorator ([a554892](https://github.com/puj-course/FIS_2610_3513_G5/pull/286/commits/a5548927bed9dab5c5211fb2ebd072ee4e1e7e31))

- **Backend y persistencia del foro de reseñas** — Valeria Gómez
  - Entidad Resena y endpoints REST ([373d041](https://github.com/puj-course/FIS_2610_3513_G5/commit/373d0417c2b9c40a42767db997d27301f2dd9f54))
  - Formulario de creación y eliminación de reseñas ([373d041](https://github.com/puj-course/FIS_2610_3513_G5/commit/373d0417c2b9c40a42767db997d27301f2dd9f54))

- **Edición del perfil de usuario** — Sarah Barrero
  - Endpoint PUT /usuario/:id ([cb31452](https://github.com/puj-course/FIS_2610_3513_G5/commit/cb3145223ca64ee0d06319589bc74879284129c2))
  - Almacenamiento de imagen de perfil ([201641c](https://github.com/puj-course/FIS_2610_3513_G5/commit/201641c06dda93aa620c368e5b5425341eb397aa))
  - Vista Mi perfil y propagación de cambios ([bdc1209](https://github.com/puj-course/FIS_2610_3513_G5/commit/bdc12097af07b2247408f1bf5ac2c5788ae75a66))

- **Recuperación de contraseña** — Matías Mendoza
  - Endpoints POST /auth/recuperar y /auth/restablecer ([d5bf20f](https://github.com/puj-course/FIS_2610_3513_G5/commit/d5bf20f0dd164b367257b7bc5871a5ab497ac213))
  - Vistas de recuperación y nueva contraseña ([d5bf20f](https://github.com/puj-course/FIS_2610_3513_G5/commit/d5bf20f0dd164b367257b7bc5871a5ab497ac213))

---

## Sprint 9 — Apr 19 – Apr 25

### Funcionalidades implementadas
- **Cierre de sesión** — Manuel Movilla
  - Invalidación del token en el back-end ([b7897da](https://github.com/puj-course/FIS_2610_3513_G5/commit/b7897dace2d6f3454e9fe672b5694615524c74ff))
  - Guard de rutas protegidas en el front ([e5a234f](https://github.com/puj-course/FIS_2610_3513_G5/commit/e5a234f784139522fd5bdc3ce04dc7bd63b71ed3))
  - Botón Cerrar sesión con modal de confirmación ([fc32574](https://github.com/puj-course/FIS_2610_3513_G5/commit/fc32574b45b3280a47d7d0977b35c65cc14c2aa0))

- **Exportar resumen académico** — Federico Mejía

- **Calendario funcional con visualización de tareas por fecha** — Sarah Barrero
  - Endpoint de tareas por rango de fechas ([4218c67](https://github.com/puj-course/FIS_2610_3513_G5/commit/4218c67b933979b4cfd006a5e10472ad9391c2f8))
  - Indicadores de tareas en el calendario ([76d7a87](https://github.com/puj-course/FIS_2610_3513_G5/commit/76d7a870a84613019d0231ae19db2fbb6abc88e6))
  - Panel de detalle al hacer clic en un día ([8fadcc7](https://github.com/puj-course/FIS_2610_3513_G5/commit/8fadcc7713c19a01a7b09addac349b458c195b0f))

- **Visualización de materias en el horario semanal según días y horas** — Manuel Movilla
  - Endpoint de materias con horario ([9db8f77](https://github.com/puj-course/FIS_2610_3513_G5/pull/451/commits/9db8f7765b840c8b8bcf3b824f31d1f1b367d477))
  - Grilla base del horario semanal ([80a02b8](https://github.com/puj-course/FIS_2610_3513_G5/pull/452/commits/80a02b8ef60655802cd9df1b8db202f376277e02))
  - Renderizado de bloques de materia ([ed85f7a](https://github.com/puj-course/FIS_2610_3513_G5/commit/ed85f7a5f08a41c23ad76997566bb1fb41b83d6e))

- **Configuración de colores del dashboard** — Federico Mejía
  - Endpoint de preferencias de color ([cb785db](https://github.com/puj-course/FIS_2610_3513_G5/commit/cb785db3b06bec42cbff5e2fb18a21802a356bdf))
  - Panel de configuración con previsualización en tiempo real ([cb785db](https://github.com/puj-course/FIS_2610_3513_G5/commit/cb785db3b06bec42cbff5e2fb18a21802a356bdf))

---

## Sprint 10 — Apr 26 – May 02

### Funcionalidades implementadas
- **Migración de base de datos de H2 a Render** — Valeria Gómez
  - Instancia PostgreSQL en Render ([59bcdc8](https://github.com/puj-course/FIS_2610_3513_G5/commit/59bcdc87ce0672010e9d2253b153336b70fe3be1))
  - Configuración del back-end para PostgreSQL ([c1ac941](https://github.com/puj-course/FIS_2610_3513_G5/commit/c1ac9419fb46b89c2b9271f97504132fed7efed6))
  - Separación de configuración local y producción ([59bcdc8](https://github.com/puj-course/FIS_2610_3513_G5/commit/59bcdc87ce0672010e9d2253b153336b70fe3be1))

- **Estadísticas académicas globales del estudiante** — Matías Mendoza
  - Endpoint de agregación de calificaciones ([5ba6811](https://github.com/puj-course/FIS_2610_3513_G5/commit/5ba6811abffa2244a600fe9e92e3cf149907d98f))
  - Gráfica comparativa de promedios por materia ([5ba6811](https://github.com/puj-course/FIS_2610_3513_G5/commit/5ba6811abffa2244a600fe9e92e3cf149907d98f))
  - Lógica de materia en riesgo y sección de alertas ([5ba6811](https://github.com/puj-course/FIS_2610_3513_G5/commit/5ba6811abffa2244a600fe9e92e3cf149907d98f))

- **Visualización de prioridades en vistas de notas y tareas** — Valeria Gómez
  - Componente PriorityBadge e integración en vistas ([11eac53](https://github.com/puj-course/FIS_2610_3513_G5/pull/519/commits/11eac531e59ab5fbe769c64b3f183a2f14867eaf))

- **Horario semanal expandido** — Federico Mejía
  - Implementación de endpoints de horario semanal ([c6c94f7](https://github.com/puj-course/FIS_2610_3513_G5/commit/c6c94f751bc66b8f5dd69de0f23c809eaabf0718))
  - Vista de tabla semanal y exportación CSV/PDF ([c6c94f7](https://github.com/puj-course/FIS_2610_3513_G5/commit/c6c94f751bc66b8f5dd69de0f23c809eaabf0718))

- **Sistema de notificaciones interactivas** — Manuel Movilla
  - Entidad Notification en BD ([d2b58f1](https://github.com/puj-course/FIS_2610_3513_G5/commit/d2b58f1b8bcc4417836621ad2dba1c9bbaac7879))
  - Endpoints REST y canal en tiempo real ([4b65e53](https://github.com/puj-course/FIS_2610_3513_G5/commit/4b65e53eb83c1ade714da786b6e8e7afba2d7eb8))
  - Componente NotificationPanel ([047ef15](https://github.com/puj-course/FIS_2610_3513_G5/commit/047ef15e583177a0b0fc3d80bd4a8a3400e8aea9))

---

## Sprint 11 — May 10 – May 16

### Funcionalidades implementadas
- **Sistema de filtrado de horario y búsqueda** — Matías Mendoza

- **Gestión de imagen de perfil y cuadrícula de materias** — Sarah Barrero
  - Endpoint de gestión de imagen de perfil ([d82336a](https://github.com/puj-course/FIS_2610_3513_G5/commit/d82336abcc7dd931e5ae3c161b7b333a9ce87865))
  - Endpoint de materias funcionales ([9db5a43](https://github.com/puj-course/FIS_2610_3513_G5/commit/9db5a433f543e8664b44ed1e3411b8b2955b5f29))

- **Redirección inteligente desde notificación** — Valeria Gómez
  - Estructura de targetUrl en el modelo Notification ([fd07f97](https://github.com/puj-course/FIS_2610_3513_G5/commit/fd07f977951f4b4cfa91386e49d9f32a5474a04d))
  - Módulo de resolución de URLs en el frontend ([fd07f97](https://github.com/puj-course/FIS_2610_3513_G5/commit/fd07f977951f4b4cfa91386e49d9f32a5474a04d))

- **Exportación de horario (PDF/Imagen)** — Sarah Barrero
  - Integración de librerías de exportación en el frontend ([33e2bf1](https://github.com/puj-course/FIS_2610_3513_G5/commit/33e2bf1804ff9c43488dac1de50820a727fd69e5))
  - Exportación a imagen JPG y PDF ([98ebbd3](https://github.com/puj-course/FIS_2610_3513_G5/commit/98ebbd3b5893eb694dce96a3b8f8a9f620a2b64d))
  - Botones de exportación en la UI del horario ([98ebbd3](https://github.com/puj-course/FIS_2610_3513_G5/commit/98ebbd3b5893eb694dce96a3b8f8a9f620a2b64d))

- **Vista de detalle y salón en el horario** — Manuel Movilla
  - Extensión del modelo y endpoint de horario ([e631fe2](https://github.com/puj-course/FIS_2610_3513_G5/commit/e631fe2c3493d87ad44ff35a2450c5e518ff0554))
  - Modal de detalle de evento en el frontend ([3d5e4e2](https://github.com/puj-course/FIS_2610_3513_G5/commit/3d5e4e2074ed3f1b323b13494a7cdf97f69f73af))
  - Vista de detalle del salón ([55e5de7](https://github.com/puj-course/FIS_2610_3513_G5/commit/55e5de76dae7abe155533332b7aba07fda5522b4))
  - Pruebas del flujo completo ([33adae3](https://github.com/puj-course/FIS_2610_3513_G5/commit/33adae3c4485a2c462908a6b0795407368878d9c))