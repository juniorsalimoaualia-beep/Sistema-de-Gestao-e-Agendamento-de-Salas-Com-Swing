package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller;

import java.util.List;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.GestorReservaDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao.ReservaDAO;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Reserva;

public class ReservaController {
    private ReservaDAO reservaDAO = new ReservaDAO();
    private GestorReservaDAO gestorDAO = new GestorReservaDAO();

    public boolean salvar(Reserva reserva) {
        return reservaDAO.salvar(reserva);
    }

    public void vincularSalaAReserva(int salaId, int reservaId) {
        reservaDAO.vincularSalaAReserva(salaId, reservaId);
    }

    public List<Reserva> listar() {
        return reservaDAO.listarReservas();
    }

    public Reserva buscarPorId(int id) {
        return reservaDAO.buscarPorReserva(id);
    }

    public boolean verificarDisponibilidade(String nomeSala, java.time.LocalDate data, java.time.LocalTime inicio, java.time.LocalTime fim) {
        return gestorDAO.verificarDisponibilidade(nomeSala, data, inicio, fim);
    }

    public boolean confirmar(int id) {
        return gestorDAO.confirmarReserva(id);
    }

    public boolean cancelar(int id) {
        return gestorDAO.cancelarReserva(id);
    }

    public boolean cancelarDoDocente(int reservaId, int docenteId) {
        Reserva reserva = buscarPorId(reservaId);
        if (reserva == null) {
            return false;
        }
        try {
            main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Docente d = reserva.getDocenteId();
            if (d == null || d.getId() != docenteId) return false;
        } catch (Exception e) {
            return false;
        }
        return gestorDAO.cancelarReserva(reservaId);
    }

    public String estadoReserva(int id) {
        Reserva reserva = buscarPorId(id);
        if (reserva == null) {
            return "Reserva nao encontrada";
        }
        String salaInfo = "";
        try {
            salaInfo = reserva.getSalaId() != null ? String.valueOf(reserva.getSalaId().getId()) : "";
        } catch (Exception e) { salaInfo = ""; }
        return "Reserva ID: " + reserva.getId()
                + " | Estado: " + reserva.getEstadoReserva()
                + " | Sala ID: " + salaInfo
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
