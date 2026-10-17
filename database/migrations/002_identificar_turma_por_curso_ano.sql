USE sgsas;

ALTER TABLE turmas
    MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT,
    DROP INDEX uq_turmas_ano,
    ADD UNIQUE KEY uq_turmas_ano_curso (ano, curso_id);
