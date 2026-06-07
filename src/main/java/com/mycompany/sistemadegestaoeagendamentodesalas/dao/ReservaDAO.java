package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Sala;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Reserva;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.EstadoReserva;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Disciplina;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Turma;
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
    private DisciplinaDAO disciplinaDAO = new DisciplinaDAO();
    
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
                if(dados.length >= 8){
                    int id = Integer.parseInt(dados[0].trim());
                    int salaId = parseIntSafe(dados[1].trim());
                    int docenteId = parseIntSafe(dados[2].trim());

                    // Detect format: new format has disciplinaId at index 3 (numeric),
                    // old format has docenteNome at index 3 (non-numeric) and disciplina name at index 4
                    Disciplina disciplina = null;
                    Turma turma = null;
                    LocalDate data = null;
                    LocalTime horaInicio = null;
                    LocalTime horaFim = null;

                    String fourth = dados.length > 3 ? dados[3].trim() : "";
                    boolean fourthIsNumber = isInteger(fourth);

                    if (fourthIsNumber) {
                        // new format
                        int disciplinaId = Integer.parseInt(fourth);
                        String turmaChave = dados.length > 4 ? dados[4].trim() : "0-";
                        data = dados.length > 5 ? LocalDate.parse(dados[5].trim()) : null;
                        horaInicio = dados.length > 6 ? LocalTime.parse(dados[6].trim()) : null;
                        horaFim = dados.length > 7 ? LocalTime.parse(dados[7].trim()) : null;

                        disciplina = disciplinaDAO.buscarPorId(disciplinaId);
                        if (disciplina == null) disciplina = new Disciplina(disciplinaId, "", null);

                        String[] turmaParts = turmaChave.split("-");
                        try {
                            turma = new Turma(Integer.parseInt(turmaParts[0]), turmaParts[1]);
                        } catch (Exception e) {
                            turma = new Turma(0, "");
                        }
                    } else {
                        // old format
                        // indices: 3=docenteNome,4=disciplinaNome,5=turma,6=data,7=horaInicio,8=horaFim
                        String disciplinaNome = dados.length > 4 ? dados[4].trim() : "";
                        String turmaChave = dados.length > 5 ? dados[5].trim() : "0-";
                        data = dados.length > 6 ? LocalDate.parse(dados[6].trim()) : null;
                        horaInicio = dados.length > 7 ? LocalTime.parse(dados[7].trim()) : null;
                        horaFim = dados.length > 8 ? LocalTime.parse(dados[8].trim()) : null;

                        disciplina = disciplinaDAO.buscarPorNome(disciplinaNome);
                        if (disciplina == null) disciplina = new Disciplina(0, disciplinaNome, null);

                        String[] turmaParts = turmaChave.split("-");
                        try {
                            turma = new Turma(Integer.parseInt(turmaParts[0]), turmaParts[1]);
                        } catch (Exception e) {
                            turma = new Turma(0, "");
                        }
                    }

                    Reserva reserva = new Reserva(id, salaId, docenteId, disciplina, turma, data, horaInicio, horaFim);

                    // estado pode estar em posição diferente dependendo do formato; try to read last token
                    String estadoToken = dados[dados.length - 1].trim();
                    try {
                        reserva.setEstadoReserva(EstadoReserva.valueOf(estadoToken));
                    } catch (Exception e) {
                        // default to PENDENTE
                        reserva.setEstadoReserva(EstadoReserva.PENDENTE);
                    }

                    listar.add(reserva);
                }
            }
        }
        catch(IOException e){System.out.println("Erro ao listar "+e.getMessage());}
        return listar;
    }

    private boolean isInteger(String s) {
        if (s == null || s.isEmpty()) return false;
        try { Integer.parseInt(s); return true; } catch (NumberFormatException e) { return false; }
    }

    private int parseIntSafe(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
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

    public void reescreverArquivo(List<Reserva> lista) {
        File arquivo = ArquivoUtils.prepararArquivo(file);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, false))) {
            for (Reserva r : lista) {
                bw.write(r.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao reescrever arquivo de Reservas: " + e.getMessage());
        }
    }
}
