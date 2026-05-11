package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller;

import java.util.List;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.TurmaDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Turma;

public class TurmaController {
    private TurmaDAO dao = new TurmaDAO();

    public void salvar(Turma turma) {
        dao.salvar(turma);
    }

    public List<Turma> listar() {
        return dao.listaTurma();
    }

    public String buscarPorChave(String chave) {
        return dao.buscarPorTurma(chave);
    }

    public int gerarProximoId() {
        return dao.gerarProximoId();
    }
}
