USE sgsas;

-- Existing departments without a secretary can be assigned in Admin > Departamentos.
ALTER TABLE horarios
    ADD COLUMN sala_id INT NULL AFTER disciplina_id,
    ADD KEY ix_horarios_sala (sala_id);

ALTER TABLE departamentos
    ADD COLUMN secretario_id INT NULL AFTER sala_id,
    ADD UNIQUE KEY uq_departamentos_secretario (secretario_id);
