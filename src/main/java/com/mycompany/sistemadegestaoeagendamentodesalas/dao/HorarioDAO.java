package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;

import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.DiaSemana;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Horario;

public class HorarioDAO {
    private static final String file ="files/Horario.txt";

    public void salvar(Horario dp){
        try(BufferedWriter bw= new BufferedWriter(new FileWriter(file, true))){
            bw.write(dp.toString());
            bw.newLine();
        }catch(IOException e){System.out.println("Erro ao salvar Horario "+e.getMessage());}
    }

    public List<Horario> listaHorario(){
        List<Horario> lista=new ArrayList<>();
        String linha;
        try(BufferedReader br= new BufferedReader(new FileReader(file))){
            while((linha=br.readLine())!=null){
                String []dados=linha.split("; ");
                if(dados.length==5){
                    DiaSemana dia = DiaSemana.valueOf(dados[2].trim());
                    lista.add(new Horario(
                        Integer.parseInt(dados[0]),
                        dados[1],
                        dia,
                        LocalTime.parse(dados[3]),
                        LocalTime.parse(dados[4])));
                }  
            }
        }catch(IOException e){System.out.println("Erro ao ler o ficheiro "+e.getMessage());}
        return lista;
    }

    public String buscarPorHorario(String curso){
        List<Horario> lista=listaHorario();
        String nome;
        LocalTime inicio;
        LocalTime fim;
        for(Horario hr:lista){
            if(curso.equalsIgnoreCase(hr.getNomeCurso())){
                nome=hr.getNomeCurso();
                inicio= hr.getHoraInicio();
                fim= hr.getHoraFim();
                return "Curso: "+nome+" |Inicio: "+inicio+" |Fim: "+fim;
            }
        }
        return "Horario nao encontrado";
    }

    public int gerarProximoId(){
        int max = 0;
        for(Horario hr : listaHorario()){
            max = Math.max(max, hr.getId());
        }
        return max + 1;
    }
}
