package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Sala;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Docente;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Reserva;
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
        SalaDAO salaDao=new SalaDAO();
        DocenteDAO dcDAO=new DocenteDAO();
        
        File arquivo = new File(file);
        if (!arquivo.exists()) {
            return listar;
        }
        try(BufferedReader br= new BufferedReader(new FileReader(arquivo))){
            String linha;
            
            while((linha=br.readLine())!=null){
                String dados []= linha.split("; ");
                String nomeSala= dados[1].trim();
                int idDocente=Integer.parseInt(dados[2].trim());
                if(dados.length>=7){
                    Sala sala =  salaDao.buscarPorSala(nomeSala);
                    Docente docente =  dcDAO.buscarPorDocente(idDocente);
                    listar.add(new Reserva(
                        Integer.parseInt(dados[0]),
                        sala,
                        docente,
                        dados[3],
                        "",
                        LocalDate.parse(dados[4].trim()),
                        LocalTime.parse(dados[5].trim()),
                        LocalTime.parse(dados[6].trim())));
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

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (Reserva reserva : lista) {
                if (reserva.getId() == id) {
                    encontrado = true;
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
}
