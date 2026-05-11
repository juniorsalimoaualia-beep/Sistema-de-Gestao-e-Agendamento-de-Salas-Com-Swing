package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller;

import java.util.List;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.EstudanteDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Estudante;

public class EstudanteController {
    private EstudanteDAO dao = new EstudanteDAO();

    public void salvar(Estudante estudante) {
        dao.salvar(estudante);
    }

    public List<Estudante> listar() {
        return dao.listaEstudante();
    }

    public Estudante buscarPorId(int id) {
        return dao.buscarEstudantePorId(id);
    }

    public String buscarNomePorId(int id) {
        return dao.buscarPorEstudante(id);
    }

    public int gerarProximoId() {
        return dao.gerarProximoId();
    }
}
