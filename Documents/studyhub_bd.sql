CREATE TABLE estudiantes (
    id_estudiante INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) UNIQUE NOT NULL,
    carrera VARCHAR(100),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE asignaturas (
    id_asignatura INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    profesor VARCHAR(100),
    creditos INT,
    codigo INT
);
CREATE TABLE matriculas (
    id_matricula INT AUTO_INCREMENT PRIMARY KEY,
    id_estudiante INT,
    id_asignatura INT,
    fecha_matricula DATE,
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante),
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id_asignatura)
);
CREATE TABLE notas (
    id_nota INT AUTO_INCREMENT PRIMARY KEY,
    id_matricula INT,
    descripcion VARCHAR(100),
    nota DECIMAL(4,2),
    fecha DATE,
    FOREIGN KEY (id_matricula) REFERENCES matriculas(id_matricula)
);
CREATE TABLE calendario (
    id_evento INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150),
    descripcion TEXT,
    fecha_inicio DATETIME,
    fecha_fin DATETIME,
    id_estudiante INT,
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante)
);
CREATE TABLE notificaciones (
    id_notificacion INT AUTO_INCREMENT PRIMARY KEY,
    mensaje TEXT,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    leida BOOLEAN DEFAULT FALSE,
    id_estudiante INT,
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante)
);
CREATE TABLE horario (
    id_horario INT AUTO_INCREMENT PRIMARY KEY,
    id_asignatura INT,
    id_estudiante INT,
    dia_semana ENUM('Lunes','Martes','Miercoles','Jueves','Viernes','Sabado'),
    hora_inicio TIME,
    hora_fin TIME,
    aula VARCHAR(50),
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id_asignatura),
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante)
);
ALTER TABLE matriculas
ADD COLUMN periodo VARCHAR(10),
ADD COLUMN estado ENUM('Activa','Aprobada','Reprobada','Cancelada');
