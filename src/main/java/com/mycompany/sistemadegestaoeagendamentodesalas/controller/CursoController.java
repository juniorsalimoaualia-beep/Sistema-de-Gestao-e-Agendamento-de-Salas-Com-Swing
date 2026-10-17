package com.mycompany.sistemadegestaoeagendamentodesalas.controller;

import java.util.List;
import com.mycompany.sistemadegestaoeagendamentodesalas.dao.CursoDAO;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Curso;

public class CursoController implements Controller<Curso> {
    private CursoDAO dao = new CursoDAO();

    public void salvar(Curso curso) {
        dao.salvar(curso);
    }

    public List<Curso> listar() {
        return dao.listaCurso();
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
