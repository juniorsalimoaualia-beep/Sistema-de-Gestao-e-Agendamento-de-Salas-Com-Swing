CREATE DATABASE IF NOT EXISTS sgsas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sgsas;

-- Foreign keys are omitted because the current DAOs replace users by profile
-- and use -1 as the value for a room without a reservation.
CREATE TABLE IF NOT EXISTS usuarios (
    id INT NOT NULL AUTO_INCREMENT,
    perfil ENUM('ADMIN', 'SECRETARIO', 'DOCENTE', 'ESTUDANTE') NOT NULL,
    nome VARCHAR(100) NOT NULL,
    apelido VARCHAR(100) NOT NULL DEFAULT '',
    nivel_academico VARCHAR(100) NULL,
    cargo VARCHAR(100) NULL,
    curso_id INT NULL,
    ano INT NULL,
    celular INT NOT NULL DEFAULT 0,
    email VARCHAR(190) NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_usuarios_perfil_email (perfil, email),
    KEY ix_usuarios_curso (curso_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS departamentos (
    id INT NOT NULL,
    nome VARCHAR(150) NOT NULL,
    curso_id INT NULL,
    sala_id INT NULL,
    PRIMARY KEY (id),
    KEY ix_departamentos_curso (curso_id),
    KEY ix_departamentos_sala (sala_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS cursos (
    id INT NOT NULL,
    nome VARCHAR(150) NOT NULL,
    departamento_id INT NULL,
    PRIMARY KEY (id),
    KEY ix_cursos_nome (nome),
    KEY ix_cursos_departamento (departamento_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS disciplinas (
    id INT NOT NULL,
    nome VARCHAR(150) NOT NULL,
    docente_id INT NULL,
    curso_id INT NULL,
    PRIMARY KEY (id),
    KEY ix_disciplinas_nome (nome),
    KEY ix_disciplinas_docente (docente_id),
    KEY ix_disciplinas_curso (curso_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS salas (
    id INT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    estado ENUM('LIVRE', 'OCUPADA', 'MANUTENCAO', 'RESERVADA') NOT NULL DEFAULT 'LIVRE',
    reserva_id INT NOT NULL DEFAULT -1,
    departamento_id INT NULL,
    PRIMARY KEY (id),
    KEY ix_salas_nome (nome),
    KEY ix_salas_reserva (reserva_id),
    KEY ix_salas_departamento (departamento_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS turmas (
    id INT NOT NULL AUTO_INCREMENT,
    ano INT NOT NULL,
    curso_id INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_turmas_ano_curso (ano, curso_id),
    KEY ix_turmas_curso (curso_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS turma_estudantes (
    turma_id INT NOT NULL,
    estudante_id INT NOT NULL,
    PRIMARY KEY (turma_id, estudante_id),
    KEY ix_turma_estudantes_estudante (estudante_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS inscricoes (
    id INT NOT NULL,
    estudante_id INT NOT NULL,
    disciplina_id INT NOT NULL,
    PRIMARY KEY (id),
    KEY ix_inscricoes_estudante (estudante_id),
    KEY ix_inscricoes_disciplina (disciplina_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS horarios (
    id INT NOT NULL,
    curso_id INT NULL,
    disciplina_id INT NULL,
    dia_semana ENUM('SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA', 'SABADO') NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    PRIMARY KEY (id),
    KEY ix_horarios_curso (curso_id),
    KEY ix_horarios_disciplina (disciplina_id),
    KEY ix_horarios_dia_hora (dia_semana, hora_inicio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS reservas (
    id INT NOT NULL,
    sala_id INT NOT NULL,
    docente_id INT NOT NULL,
    disciplina_id INT NULL,
    turma_id INT NULL,
    data DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    tipo ENUM('AULA', 'EVENTO', 'EXAME', 'TESTE') NOT NULL,
    dia_semana ENUM('SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA', 'SABADO') NOT NULL,
    estado ENUM('PENDENTE', 'CONFIRMADA', 'RECUSADA', 'CANCELADA') NOT NULL DEFAULT 'PENDENTE',
    PRIMARY KEY (id),
    KEY ix_reservas_sala_data (sala_id, data),
    KEY ix_reservas_docente (docente_id),
    KEY ix_reservas_disciplina (disciplina_id),
    KEY ix_reservas_turma (turma_id),
    KEY ix_reservas_intervalo (sala_id, data, hora_inicio, hora_fim)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
