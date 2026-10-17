package com.mycompany.sistemadegestaoeagendamentodesalas.controller;

import java.util.List;
import com.mycompany.sistemadegestaoeagendamentodesalas.dao.SalaDAO;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Sala;

public class SalaController implements Controller<Sala> {
    private SalaDAO dao = new SalaDAO();

    public void salvar(Sala sala) {
        dao.salvar(sala);
    }

    public List<Sala> listar() {
        return dao.listaSala();
    }

    public Sala buscarPorNome(String nome) {
        return dao.buscarPorSala(nome);
    }

    public Sala buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    public int gerarProximoId() {
        return dao.gerarProximoId();
    }
}
