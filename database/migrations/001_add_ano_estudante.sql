USE sgsas;

ALTER TABLE usuarios
    ADD COLUMN ano INT NULL AFTER curso_id;
