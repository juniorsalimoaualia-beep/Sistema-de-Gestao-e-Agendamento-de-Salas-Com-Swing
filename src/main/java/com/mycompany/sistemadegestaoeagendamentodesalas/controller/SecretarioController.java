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

    public Secretario buscarPorId(int id) {
        return dao.buscarPorSecretario(id);
    }

    public int gerarProximoId() {
        return dao.gerarProximoId();
    }

    public boolean alterarSenha(int id, String senhaAtual, String senhaNova) {
        return dao.alterarSenha(id, senhaAtual, senhaNova);
    }

    public void editarSecretario(int id, String nome, String apelido, String cargo, int numCel, String email) {
        List<Secretario> lista = dao.listaSecretario();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == id) {
                Secretario secretario = lista.get(i);
                secretario.setNome(nome);
                secretario.setApelido(apelido);
                secretario.setCargo(cargo);
                secretario.setNumCel(numCel);
                secretario.setEmail(email);
                dao.reescreverArquivo(lista);
                return;
            }
        }
    }

    public boolean deletarSecretario(int id) {
        List<Secretario> lista = dao.listaSecretario();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == id) {
                lista.remove(i);
                dao.reescreverArquivo(lista);
                return true;
            }
        }
        return false;
    }
}
