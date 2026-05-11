package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller;

import java.util.List;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.DepartamentoDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Departamento;

public class DepartamentoController {
    private DepartamentoDAO dao = new DepartamentoDAO();

    public void salvar(Departamento departamento) {
        dao.salvar(departamento);
    }

    public List<Departamento> listar() {
        return dao.listaDepartamento();
    }

    public String buscarPorId(int id) {
        return dao.buscarPorDepartamento(id);
    }

    public int gerarProximoId() {
        return dao.gerarProximoId();
    }
}
