package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller;

import java.util.List;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.DisciplinaDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.DocenteDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Disciplina;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Docente;

public class DisciplinaController {
    private DisciplinaDAO dao = new DisciplinaDAO();
    private DocenteDAO docenteDAO = new DocenteDAO();

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
        Docente docente = docenteDAO.buscarPorDocente(docenteId);
        if(docente != null) {
            return dao.listarPorDocente(docente);
        }
        return java.util.Collections.emptyList();
    }

    public List<Disciplina> listarPorCurso(String curso) {
        return dao.listarPorCurso(curso);
    }

    public int gerarProximoId() {
        return dao.gerarProximoId();
    }
}
