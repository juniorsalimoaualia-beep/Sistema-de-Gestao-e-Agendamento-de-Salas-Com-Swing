package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller;

import java.util.List;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.GestorReservaDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.ReservaDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Reserva;

public class ReservaController {
    private ReservaDAO reservaDAO = new ReservaDAO();
    private GestorReservaDAO gestorDAO = new GestorReservaDAO();

    public void salvar(Reserva reserva) {
        reservaDAO.salvar(reserva);
    }

    public List<Reserva> listar() {
        return reservaDAO.listarReservas();
    }

    public Reserva buscarPorId(int id) {
        return reservaDAO.buscarPorReserva(id);
    }

    public boolean confirmar(int id) {
        return gestorDAO.confirmarReserva(id);
    }

    public boolean cancelar(int id) {
        return gestorDAO.cancelarReserva(id);
    }

    public boolean cancelarDoDocente(int reservaId, int docenteId) {
        Reserva reserva = buscarPorId(reservaId);
        if (reserva == null || reserva.getDocenteId() != docenteId) {
            return false;
        }
        return gestorDAO.cancelarReserva(reservaId);
    }

    public String estadoReserva(int id) {
        Reserva reserva = buscarPorId(id);
        if (reserva == null) {
            return "Reserva nao encontrada";
        }
        return "Reserva ID: " + reserva.getId()
                + " | Estado: " + reserva.getEstadoReserva()
                + " | Sala ID: " + reserva.getSalaId()
                + " | Disciplina: " + (reserva.getDisciplina() != null ? reserva.getDisciplina().getNome() : "")
                + " | Turma: " + reserva.getTurma()
                + " | Data: " + reserva.getData()
                + " | Horario: " + reserva.getHoraInicio() + "-" + reserva.getHoraFim();
    }

    public boolean excluir(int id) {
        return gestorDAO.excluirReserva(id);
    }

    public int gerarProximoId() {
        return reservaDAO.gerarProximoId();
    }
}
