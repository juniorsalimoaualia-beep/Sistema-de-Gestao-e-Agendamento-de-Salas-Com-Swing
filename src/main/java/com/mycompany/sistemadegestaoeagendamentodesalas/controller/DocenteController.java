package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller;

import java.util.List;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.DocenteDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Docente;

public class DocenteController {
    private DocenteDAO dao = new DocenteDAO();

    public void salvar(Docente docente) {
        dao.salvar(docente);
    }

    public List<Docente> listar() {
        return dao.listaDocente();
    }

    public Docente buscarPorId(int id) {
        return dao.buscarPorDocente(id);
    }

    public int gerarProximoId() {
        return dao.gerarProximoId();
    }

    public boolean alterarSenha(int id, String senhaAtual, String senhaNova) {
        return dao.alterarSenha(id, senhaAtual, senhaNova);
    }

    public void editarDocente(int id, String nome, String apelido, String nivelAcademico, int numCel, String email) {
        List<Docente> lista = dao.listaDocente();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == id) {
                Docente docente = lista.get(i);
                docente.setNome(nome);
                docente.setApelido(apelido);
                docente.setNivelAcademico(nivelAcademico);
                docente.setNumCel(numCel);
                docente.setEmail(email);
                dao.reescreverArquivo(lista);
                return;
            }
        }
    }

    public boolean deletarDocente(int id) {
        List<Docente> lista = dao.listaDocente();
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
