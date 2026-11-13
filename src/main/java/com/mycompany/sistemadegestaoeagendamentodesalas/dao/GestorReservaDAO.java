package com.mycompany.sistemadegestaoeagendamentodesalas.dao;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.EstadoReserva;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.EstadoSala;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Sala;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Reserva;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.DiaSemana;
import java.time.LocalDate;
import java.time.LocalTime;



public class GestorReservaDAO {
    private final SalaDAO salaDao = new SalaDAO();
    private final ReservaDAO rsDAO = new ReservaDAO();

    public boolean verificarDisponibilidade(String nomeSala, LocalDate data, LocalTime inicio, LocalTime fim){
        if (data == null || data.isBefore(LocalDate.now()) || inicio == null || fim == null
                || data.getDayOfWeek() == java.time.DayOfWeek.SUNDAY || !fim.isAfter(inicio)) {
            return false;
        }
        Sala sala = salaDao.buscarPorSala(nomeSala);
        if(sala==null){
            System.out.println("Erro: Sala nao existe.");
            return false;
        }
        if(sala.getEstado()==EstadoSala.MANUTENCAO){
            System.out.println("Erro: Sala em Manutencao.");
            return false;
        }
        return !rsDAO.temConflito(sala.getId(), null, null, data, inicio, fim, 0);
    }

    public boolean verificarDisponibilidade(Reserva reserva) {
        if (reserva == null || reserva.getSalaId() == null || reserva.getDocenteId() == null
                || reserva.getSalaId().getId() <= 0 || reserva.getDocenteId().getId() <= 0
                || (reserva.getTurma() != null && reserva.getTurma().getId() <= 0)
                || reserva.getData() == null || reserva.getData().isBefore(LocalDate.now())
                || reserva.getHoraInicio() == null || reserva.getHoraFim() == null
                || !reserva.getHoraFim().isAfter(reserva.getHoraInicio())
                || reserva.getTipoReserva() == null
                || reserva.getDiaSemana() == null
                || !reserva.getTipoReserva().permiteDia(reserva.getDiaSemana())
                || !dataCorrespondeAoDia(reserva.getData(), reserva.getDiaSemana())) {
            return false;
        }
        Sala sala = salaDao.buscarPorId(reserva.getSalaId().getId());
        if (sala == null || sala.getEstado() == EstadoSala.MANUTENCAO) {
            return false;
        }

        Integer turmaId = reserva.getTurma() == null ? null : reserva.getTurma().getId();
        return !rsDAO.temConflito(sala.getId(), reserva.getDocenteId().getId(), turmaId,
                reserva.getData(), reserva.getHoraInicio(), reserva.getHoraFim(), reserva.getId());
    }

    private boolean dataCorrespondeAoDia(LocalDate data, DiaSemana dia) {
        switch (data.getDayOfWeek()) {
            case MONDAY: return dia == DiaSemana.SEGUNDA;
            case TUESDAY: return dia == DiaSemana.TERCA;
            case WEDNESDAY: return dia == DiaSemana.QUARTA;
            case THURSDAY: return dia == DiaSemana.QUINTA;
            case FRIDAY: return dia == DiaSemana.SEXTA;
            case SATURDAY: return dia == DiaSemana.SABADO;
            default: return false;
        }
    }

    public boolean confirmarReserva(int idReserva){
        Reserva reserva = rsDAO.buscarPorReserva(idReserva);
        if (reserva == null || reserva.getEstadoReserva() != EstadoReserva.PENDENTE
                || reserva.getData() == null || reserva.getData().isBefore(LocalDate.now())
                || !verificarDisponibilidade(reserva)) {
            return false;
        }
        return rsDAO.confirmarReserva(idReserva);
    }

    public boolean cancelarReserva(int idReserva){
        return rsDAO.cancelarReserva(idReserva);
    }

    public boolean recusarReserva(int idReserva) {
        return rsDAO.recusarReserva(idReserva);
    }

    public boolean excluirReserva(int idReserva){
        boolean excluido = rsDAO.excluirReserva(idReserva);
        if(excluido){
            System.out.println("Reserva ID " + idReserva + " excluida com sucesso.");
        } else {
            System.out.println("Reserva nao encontrada.");
        }
        return excluido;
    }
}
