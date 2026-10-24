package com.mycompany.sistemadegestaoeagendamentodesalas.controller;

import java.util.List;
import com.mycompany.sistemadegestaoeagendamentodesalas.dao.EstudanteDAO;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Estudante;

public class EstudanteController implements UsuarioController<Estudante> {
    private EstudanteDAO dao = new EstudanteDAO();

    public void salvar(Estudante estudante) {
        dao.salvar(estudante);
    }

    public boolean cadastrarEstudante(Estudante estudante) {
        boolean salvo = dao.salvar(estudante);
        if (!salvo) {
            return false;
        }
        return true;
    }

    public boolean verificarDuplicado(Estudante estudante) {
        return dao.isEstudanteDuplicado(estudante);
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
                dao.atualizarTodos(lista);
                return;
            }
        }
    }

    public boolean deletarEstudante(int id) {
        List<Estudante> lista = dao.listaEstudante();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == id) {
                lista.remove(i);
                dao.atualizarTodos(lista);
                return true;
            }
        }
        return false;
    }
}
