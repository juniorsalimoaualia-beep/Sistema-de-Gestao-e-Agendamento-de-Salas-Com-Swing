package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller;

import java.util.List;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.HorarioDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Horario;

public class HorarioController {
    private HorarioDAO dao = new HorarioDAO();

    public void salvar(Horario horario) {
        dao.salvar(horario);
    }

    public List<Horario> listar() {
        return dao.listaHorario();
    }

    public String buscarPorCurso(String curso) {
        return dao.buscarPorHorario(curso);
    }

    public int gerarProximoId() {
        return dao.gerarProximoId();
    }
}
