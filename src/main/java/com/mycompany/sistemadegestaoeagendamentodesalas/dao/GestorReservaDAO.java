package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.EstadoReserva;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.EstadoSala;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Sala;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Reserva;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;



public class GestorReservaDAO {
    private SalaDAO salaDao= new SalaDAO();
    private ReservaDAO rsDAO= new ReservaDAO();
    private List<Reserva> rs=rsDAO.listarReservas();

    public boolean verificarDisponibilidade(String nomeSala, LocalDate data, LocalTime inicio, LocalTime fim){
        Sala sala = salaDao.buscarPorSala(nomeSala);
        if(sala==null){
            System.out.println("Erro: Sala nao existe.");
            return false;
        }
        if(sala.getEstado()==EstadoSala.MANUTENCAO){
            System.out.println("Erro: Sala em Manutencao.");
            return false;
        }
        for(Reserva r: rs){
            boolean mesmaSala = r.getSalaId() == sala.getId();
            boolean mesmaData=r.getData().equals(data);
            boolean reservaAtiva= r.getEstadoReserva()==EstadoReserva.CONFIRMADA||r.getEstadoReserva()==EstadoReserva.PENDENTE;
            if(mesmaSala && mesmaData && reservaAtiva){
                boolean conflita = !(fim.isBefore(r.getHoraInicio()) || inicio.isAfter(r.getHoraFim()) || fim.equals(r.getHoraInicio()) || inicio.equals(r.getHoraInicio()));
                if(conflita){
                    System.out.println("Erro: Conflito com reserva ID "+r.getId());
                    return false;
                }
            }
        }
        return true;
    }

    public boolean confirmarReserva(int idReserva){
        Reserva r = rsDAO.buscarPorReserva(idReserva);
        if(r==null){
            System.out.println("Reserva nao encontrada.");
            return false;
        }
        if(r.getEstadoReserva()!=EstadoReserva.PENDENTE){
            System.out.println("So pode confirmar reserva Pendente.");
            return false;
        }
        r.setEstadoReserva(EstadoReserva.CONFIRMADA);
        return true;
    }

    public boolean cancelarReserva(int idReserva){
        Reserva r= rsDAO.buscarPorReserva(idReserva);
        if(r==null) return false;
        if(r.getEstadoReserva()==EstadoReserva.CONFIRMADA){
            System.out.println("Nao pode ser cancelada, a resserva foi confirmada.");
            return false;
        }
        r.setEstadoReserva(EstadoReserva.CANCELADA);
        return true;
    }

    public boolean excluirReserva(int idReserva){
        boolean excluido = rsDAO.excluirReserva(idReserva);
        if(excluido){
            rs = rsDAO.listarReservas();
            System.out.println("Reserva ID " + idReserva + " excluida com sucesso.");
        } else {
            System.out.println("Reserva nao encontrada.");
        }
        return excluido;
    }
}
