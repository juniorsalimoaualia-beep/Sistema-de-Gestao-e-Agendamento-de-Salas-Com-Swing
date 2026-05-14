package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller;

import java.util.List;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.CursoDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Curso;

public class CursoController {
    private CursoDAO dao = new CursoDAO();

    public void salvar(Curso curso) {
        dao.salvar(curso);
    }

    public List<Curso> listar() {
        return dao.listaCurso();
    }

    public String buscarPorId(int id) {
        return dao.buscarPorCurso(id);
    }

    public Curso buscarCursoPorId(int id) {
        return dao.buscarCursoPorId(id);
    }

    public void editarCurso(int id, String novoNome) {
        dao.editarCurso(id, novoNome);
    }

    public int gerarProximoId() {
        return dao.gerarProximoId();
    }
}
