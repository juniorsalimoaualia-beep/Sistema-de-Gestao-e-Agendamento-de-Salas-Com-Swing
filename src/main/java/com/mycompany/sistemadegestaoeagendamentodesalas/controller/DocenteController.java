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
}
