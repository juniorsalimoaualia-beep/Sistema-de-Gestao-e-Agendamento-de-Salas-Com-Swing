package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller;

import java.util.List;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.InscricaoDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Inscricao;

public class InscricaoController {
    private InscricaoDAO dao = new InscricaoDAO();

    public void salvar(Inscricao inscricao) {
        dao.salvar(inscricao);
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
