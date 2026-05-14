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

    public boolean alterarSenha(int id, String senhaAtual, String senhaNova) {
        return dao.alterarSenha(id, senhaAtual, senhaNova);
    }

    public void editarEstudante(int id, String nome, String apelido, String curso, int numCel, String email) {
        List<Estudante> lista = dao.listaEstudante();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == id) {
                Estudante estudante = lista.get(i);
                estudante.setNome(nome);
                estudante.setApelido(apelido);
                estudante.setCurso(curso);
                estudante.setNumCel(numCel);
                estudante.setEmail(email);
                dao.reescreverArquivo(lista);
                return;
            }
        }
    }

    public boolean deletarEstudante(int id) {
        List<Estudante> lista = dao.listaEstudante();
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
