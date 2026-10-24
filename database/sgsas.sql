CREATE DATABASE IF NOT EXISTS sgsas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sgsas;

-- As chaves estrangeiras ficam de fora porque os DAOs substituem utilizadores por perfil
-- e usam -1 como valor da sala sem reserva associada.
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
    secretario_id INT NULL,
    PRIMARY KEY (id),
    KEY ix_departamentos_curso (curso_id),
    KEY ix_departamentos_sala (sala_id),
    UNIQUE KEY uq_departamentos_secretario (secretario_id)
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
    id INT NOT NULL AUTO_INCREMENT,
    estudante_id INT NOT NULL,
    disciplina_id INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_inscricoes_estudante_disciplina (estudante_id, disciplina_id),
    KEY ix_inscricoes_estudante (estudante_id),
    KEY ix_inscricoes_disciplina (disciplina_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS horarios (
    id INT NOT NULL,
    curso_id INT NOT NULL,
    disciplina_id INT NULL,
    sala_id INT NOT NULL,
    dia_semana ENUM('SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA', 'SABADO') NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    PRIMARY KEY (id),
    KEY ix_horarios_curso (curso_id),
    KEY ix_horarios_disciplina (disciplina_id),
    KEY ix_horarios_sala (sala_id),
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

DROP TRIGGER IF EXISTS trg_departamentos_validar_secretario_insert;
DROP TRIGGER IF EXISTS trg_departamentos_validar_secretario_update;
DROP TRIGGER IF EXISTS trg_horarios_validar_insert;
DROP TRIGGER IF EXISTS trg_horarios_validar_update;
DROP TRIGGER IF EXISTS trg_inscricoes_validar_insert;
DROP TRIGGER IF EXISTS trg_reservas_validar_insert;
DROP TRIGGER IF EXISTS trg_reservas_validar_update;

DELIMITER $$

CREATE TRIGGER trg_departamentos_validar_secretario_insert
BEFORE INSERT ON departamentos
FOR EACH ROW
BEGIN
    DECLARE v_perfil VARCHAR(20);
    IF NEW.secretario_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'O departamento deve ter um secretario.';
    END IF;
    SELECT perfil INTO v_perfil FROM usuarios WHERE id = NEW.secretario_id;
    IF v_perfil IS NULL OR v_perfil <> 'SECRETARIO' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'O secretario deve ter o perfil SECRETARIO.';
    END IF;
END$$

CREATE TRIGGER trg_departamentos_validar_secretario_update
BEFORE UPDATE ON departamentos
FOR EACH ROW
BEGIN
    DECLARE v_perfil VARCHAR(20);
    IF NEW.secretario_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'O departamento deve ter um secretario.';
    END IF;
    SELECT perfil INTO v_perfil FROM usuarios WHERE id = NEW.secretario_id;
    IF v_perfil IS NULL OR v_perfil <> 'SECRETARIO' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'O secretario deve ter o perfil SECRETARIO.';
    END IF;
END$$

CREATE TRIGGER trg_horarios_validar_insert
BEFORE INSERT ON horarios
FOR EACH ROW
BEGIN
    IF NEW.curso_id IS NULL OR NEW.sala_id IS NULL OR NEW.hora_fim <= NEW.hora_inicio THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'O horario precisa de curso, sala e intervalo de horas valido.';
    END IF;
    IF EXISTS (
        SELECT 1 FROM horarios h
        WHERE h.curso_id = NEW.curso_id
          AND h.dia_semana = NEW.dia_semana
          AND h.hora_inicio < NEW.hora_fim
          AND h.hora_fim > NEW.hora_inicio
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'O curso ja tem um horario sobreposto nesse dia.';
    END IF;
END$$

CREATE TRIGGER trg_horarios_validar_update
BEFORE UPDATE ON horarios
FOR EACH ROW
BEGIN
    IF NEW.curso_id IS NULL OR NEW.sala_id IS NULL OR NEW.hora_fim <= NEW.hora_inicio THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'O horario precisa de curso, sala e intervalo de horas valido.';
    END IF;
    IF EXISTS (
        SELECT 1 FROM horarios h
        WHERE h.curso_id = NEW.curso_id
          AND h.dia_semana = NEW.dia_semana
          AND h.id <> NEW.id
          AND h.hora_inicio < NEW.hora_fim
          AND h.hora_fim > NEW.hora_inicio
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'O curso ja tem um horario sobreposto nesse dia.';
    END IF;
END$$

CREATE TRIGGER trg_inscricoes_validar_insert
BEFORE INSERT ON inscricoes
FOR EACH ROW
BEGIN
    DECLARE v_curso_estudante INT;
    DECLARE v_perfil VARCHAR(20);
    DECLARE v_quantidade INT;
    DECLARE v_disciplina INT;

    SELECT perfil, curso_id INTO v_perfil, v_curso_estudante
    FROM usuarios WHERE id = NEW.estudante_id;
    IF v_perfil IS NULL OR v_perfil <> 'ESTUDANTE' OR v_curso_estudante IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A inscricao deve pertencer a um estudante.';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM disciplinas WHERE id = NEW.disciplina_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A disciplina seleccionada nao existe.';
    END IF;
    SELECT curso_id INTO v_disciplina FROM disciplinas WHERE id = NEW.disciplina_id;
    IF v_disciplina IS NOT NULL AND v_disciplina <> v_curso_estudante THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A disciplina deve ser geral ou do curso do estudante.';
    END IF;

    SELECT COUNT(*) INTO v_quantidade
    FROM inscricoes WHERE estudante_id = NEW.estudante_id;
    IF v_quantidade >= 7 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'O estudante nao pode ter mais de sete inscricoes.';
    END IF;

    IF EXISTS (
        SELECT 1 FROM inscricoes
        WHERE estudante_id = NEW.estudante_id AND disciplina_id = NEW.disciplina_id
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'O estudante ja esta inscrito nesta disciplina.';
    END IF;
END$$

CREATE TRIGGER trg_reservas_validar_insert
BEFORE INSERT ON reservas
FOR EACH ROW
BEGIN
    DECLARE v_dia VARCHAR(10);
    DECLARE v_estado_sala VARCHAR(20);
    DECLARE v_perfil_docente VARCHAR(20);

    SET v_dia = CASE WEEKDAY(NEW.data)
        WHEN 0 THEN 'SEGUNDA'
        WHEN 1 THEN 'TERCA'
        WHEN 2 THEN 'QUARTA'
        WHEN 3 THEN 'QUINTA'
        WHEN 4 THEN 'SEXTA'
        WHEN 5 THEN 'SABADO'
        ELSE NULL
    END;
    IF NEW.hora_fim <= NEW.hora_inicio OR v_dia IS NULL OR NEW.dia_semana <> v_dia THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A data ou o intervalo de horas da reserva e invalido.';
    END IF;
    IF NEW.tipo = 'AULA' AND NEW.disciplina_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A reserva de aula precisa de uma disciplina.';
    END IF;

    SELECT estado INTO v_estado_sala FROM salas WHERE id = NEW.sala_id;
    IF v_estado_sala IS NULL OR v_estado_sala = 'MANUTENCAO' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A sala seleccionada nao aceita reservas.';
    END IF;
    SELECT perfil INTO v_perfil_docente FROM usuarios WHERE id = NEW.docente_id;
    IF v_perfil_docente IS NULL OR v_perfil_docente <> 'DOCENTE' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A reserva deve pertencer a um docente.';
    END IF;

    IF NEW.estado IN ('PENDENTE', 'CONFIRMADA') AND EXISTS (
        SELECT 1 FROM reservas r
        WHERE r.data = NEW.data
          AND r.estado IN ('PENDENTE', 'CONFIRMADA')
          AND (r.sala_id = NEW.sala_id
               OR r.docente_id = NEW.docente_id
               OR (NEW.turma_id IS NOT NULL AND r.turma_id = NEW.turma_id))
          AND r.hora_inicio < NEW.hora_fim
          AND r.hora_fim > NEW.hora_inicio
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A reserva tem conflito com outra reserva activa.';
    END IF;
END$$

CREATE TRIGGER trg_reservas_validar_update
BEFORE UPDATE ON reservas
FOR EACH ROW
BEGIN
    DECLARE v_dia VARCHAR(10);
    DECLARE v_estado_sala VARCHAR(20);
    DECLARE v_perfil_docente VARCHAR(20);

    SET v_dia = CASE WEEKDAY(NEW.data)
        WHEN 0 THEN 'SEGUNDA'
        WHEN 1 THEN 'TERCA'
        WHEN 2 THEN 'QUARTA'
        WHEN 3 THEN 'QUINTA'
        WHEN 4 THEN 'SEXTA'
        WHEN 5 THEN 'SABADO'
        ELSE NULL
    END;
    IF NEW.hora_fim <= NEW.hora_inicio OR v_dia IS NULL OR NEW.dia_semana <> v_dia THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A data ou o intervalo de horas da reserva e invalido.';
    END IF;
    IF NEW.tipo = 'AULA' AND NEW.disciplina_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A reserva de aula precisa de uma disciplina.';
    END IF;

    SELECT estado INTO v_estado_sala FROM salas WHERE id = NEW.sala_id;
    IF v_estado_sala IS NULL OR v_estado_sala = 'MANUTENCAO' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A sala seleccionada nao aceita reservas.';
    END IF;
    SELECT perfil INTO v_perfil_docente FROM usuarios WHERE id = NEW.docente_id;
    IF v_perfil_docente IS NULL OR v_perfil_docente <> 'DOCENTE' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A reserva deve pertencer a um docente.';
    END IF;

    IF NEW.estado IN ('PENDENTE', 'CONFIRMADA') AND EXISTS (
        SELECT 1 FROM reservas r
        WHERE r.data = NEW.data
          AND r.id <> NEW.id
          AND r.estado IN ('PENDENTE', 'CONFIRMADA')
          AND (r.sala_id = NEW.sala_id
               OR r.docente_id = NEW.docente_id
               OR (NEW.turma_id IS NOT NULL AND r.turma_id = NEW.turma_id))
          AND r.hora_inicio < NEW.hora_fim
          AND r.hora_fim > NEW.hora_inicio
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A reserva tem conflito com outra reserva activa.';
    END IF;
END$$

DELIMITER ;
