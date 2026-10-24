package com.mycompany.sistemadegestaoeagendamentodesalas.controller;

import java.util.List;
import com.mycompany.sistemadegestaoeagendamentodesalas.dao.InscricaoDAO;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Inscricao;

public class InscricaoController implements Controller<Inscricao> {
    private InscricaoDAO dao = new InscricaoDAO();

    public boolean salvar(Inscricao inscricao) {
        return dao.salvar(inscricao);
    }

    public List<Inscricao> listarPorEstudante(int estudanteId) {
        return dao.buscarPorEstudante(estudanteId);
    }

    public List<Inscricao> listarPorDisciplina(int disciplinaId) {
        return dao.buscarPorDisciplina(disciplinaId);
    }

    public List<Inscricao> listar() {
        return dao.listaInscricao();
    }

    public Inscricao buscarPorId(int id) {
        return dao.buscarPorInscricao(id);
    }

    public int gerarProximoId() {
        return dao.gerarProximoId();
    }
}
