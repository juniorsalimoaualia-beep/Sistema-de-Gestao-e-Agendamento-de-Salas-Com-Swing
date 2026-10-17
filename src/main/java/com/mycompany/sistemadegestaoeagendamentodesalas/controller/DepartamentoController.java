package com.mycompany.sistemadegestaoeagendamentodesalas.controller;

import java.util.List;
import com.mycompany.sistemadegestaoeagendamentodesalas.dao.DepartamentoDAO;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Departamento;

public class DepartamentoController implements Controller<Departamento> {
    private DepartamentoDAO dao = new DepartamentoDAO();

    public void salvar(Departamento departamento) {
        dao.salvar(departamento);
    }

    public List<Departamento> listar() {
        return dao.listaDepartamento();
    }

    public Departamento buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    public String buscarNomePorId(int id) {
        return dao.buscarPorDepartamento(id);
    }

    public int gerarProximoId() {
        return dao.gerarProximoId();
    }
}
