package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller;

import java.util.List;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.SecretarioDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Secretario;

public class SecretarioController {
    private SecretarioDAO dao = new SecretarioDAO();

    public void salvar(Secretario secretario) {
        dao.salvar(secretario);
    }

    public List<Secretario> listar() {
        return dao.listaSecretario();
    }

    public String buscarPorId(int id) {
        return dao.buscarPorSecretario(id);
    }

    public int gerarProximoId() {
        return dao.gerarProximoId();
    }
}
