// Dados: classe TurmaController

package com.mycompany.sistemadegestaoeagendamentodesalas.controller;

import java.util.List;
import com.mycompany.sistemadegestaoeagendamentodesalas.dao.TurmaDAO;
import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Turma;

public class TurmaController {
    private TurmaDAO dao = new TurmaDAO();

    public void salvar(Turma turma) {
        if (turma == null) {
            return;
        }
        if (!dao.turmaExiste(turma.getChave())) {
            dao.salvar(turma);
        }
    }

    public boolean existePorChave(String chave) {
        return dao.turmaExiste(chave);
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
