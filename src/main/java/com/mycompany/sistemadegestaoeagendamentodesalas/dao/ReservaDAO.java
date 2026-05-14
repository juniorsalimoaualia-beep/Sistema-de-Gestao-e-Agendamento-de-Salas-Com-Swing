package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Sala;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Reserva;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.EstadoReserva;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaDAO {
    private static final String file= "files/reserva.txt";
    public void salvar(Reserva rs){
        File arquivo = ArquivoUtils.prepararArquivo(file);
        try(BufferedWriter bw =new BufferedWriter(new FileWriter(arquivo,true))){
            bw.write(rs.toString());
            bw.newLine();
        }
        catch(IOException e){System.out.println("Erro ao salvar reserva "+e.getMessage());}
    }

    public List<Reserva> listarReservas(){
        List<Reserva> listar= new ArrayList<>();
        
        File arquivo = new File(file);
        if (!arquivo.exists()) {
            return listar;
        }
        try(BufferedReader br= new BufferedReader(new FileReader(arquivo))){
            String linha;
            
            while((linha=br.readLine())!=null){
                String dados []= linha.split("; ");
                if(dados.length >= 9){
                    int id = Integer.parseInt(dados[0].trim());
                    int salaId = Integer.parseInt(dados[1].trim());
                    int docenteId = Integer.parseInt(dados[2].trim());
                    String docenteNome = dados[3].trim();
                    String disciplina = dados[4].trim();
                    String turma = dados[5].trim();
                    LocalDate data = LocalDate.parse(dados[6].trim());
                    LocalTime horaInicio = LocalTime.parse(dados[7].trim());
                    LocalTime horaFim = LocalTime.parse(dados[8].trim());
                    
                    Reserva reserva = new Reserva(id, salaId, docenteId, docenteNome, disciplina, turma, data, horaInicio, horaFim);
                    
                    if (dados.length >= 10) {
                        try {
                            reserva.setEstadoReserva(EstadoReserva.valueOf(dados[9].trim()));
                        } catch (IllegalArgumentException e) {
                            reserva.setEstadoReserva(EstadoReserva.PENDENTE);
                        }
                    }
                    
                    listar.add(reserva);
                }
            }
        }
        catch(IOException e){System.out.println("Erro ao listar "+e.getMessage());}
        return listar;
    }

    public Reserva buscarPorReserva(int id){
        List<Reserva> lista = listarReservas();
        for(Reserva n : lista){
            if(id == n.getId()){
                return n;
            }
        }
        return null;
    }

    public int gerarProximoId(){
        int max = 0;
        for (Reserva reserva : listarReservas()){
            max = Math.max(max, reserva.getId());
        }
        return max + 1;
    }

    public boolean excluirReserva(int id){
        List<Reserva> lista = listarReservas();
        boolean encontrado = false;
        SalaDAO salaDAO = new SalaDAO();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (Reserva reserva : lista) {
                if (reserva.getId() == id) {
                    encontrado = true;
                    // Desvincula a sala
                    Sala sala = salaDAO.buscarPorId(reserva.getSalaId());
                    if (sala != null) {
                        sala.desvinculaReserva();
                        salaDAO.atualizar(sala);
                    }
                    continue;
                }
                bw.write(reserva.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao excluir reserva: " + e.getMessage());
            return false;
        }

        return encontrado;
    }

    public void vincularSalaAReserva(int salaId, int reservaId) {
        SalaDAO salaDAO = new SalaDAO();
        Sala sala = salaDAO.buscarPorId(salaId);
        if (sala != null) {
            sala.vincularReserva(reservaId);
            salaDAO.atualizar(sala);
        }
    }

    public void desvinculaSalaDeReserva(int salaId) {
        SalaDAO salaDAO = new SalaDAO();
        Sala sala = salaDAO.buscarPorId(salaId);
        if (sala != null) {
            sala.desvinculaReserva();
            salaDAO.atualizar(sala);
        }
    }
}
