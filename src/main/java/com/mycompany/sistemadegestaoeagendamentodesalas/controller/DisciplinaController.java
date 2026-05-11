package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller;

import java.util.List;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.DisciplinaDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Disciplina;

public class DisciplinaController {
    private DisciplinaDAO dao = new DisciplinaDAO();

    public void salvar(Disciplina disciplina) {
        dao.salvar(disciplina);
    }

    public List<Disciplina> listar() {
        return dao.listaDisciplina();
    }

    public Disciplina buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    public List<Disciplina> listarPorDocente(int docenteId) {
        return dao.listarPorDocenteId(docenteId);
    }

    public int gerarProximoId() {
        return dao.gerarProximoId();
    }
}
