package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller;

import java.util.List;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.SalaDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Sala;

public class SalaController {
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

    public int gerarProximoId() {
        return dao.gerarProximoId();
    }
}
