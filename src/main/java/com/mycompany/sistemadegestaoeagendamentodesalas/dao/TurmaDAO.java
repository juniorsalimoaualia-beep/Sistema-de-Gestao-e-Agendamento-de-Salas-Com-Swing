package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Turma;

public class TurmaDAO {
    private static final String file ="files/Turma.txt";

    public void salvar(Turma dp){
        try(BufferedWriter bw= new BufferedWriter(new FileWriter(file, true))){
            bw.write(dp.toString());
            bw.newLine();
        }catch(IOException e){System.out.println("Erro ao salvar Turma "+e.getMessage());}
    }

    public List<Turma> listaTurma(){
        List<Turma> lista=new ArrayList<>();
         String linha;
        try(BufferedReader br= new BufferedReader(new FileReader(file))){
            while((linha=br.readLine())!=null){
                String []dados=linha.split("; ");
                if(dados.length==2){
                    lista.add(new Turma(
                        Integer.parseInt(dados[0]),
                        dados[1]));
                }
            }
        }catch(IOException e){System.out.println("Erro ao ler o ficheiro "+e.getMessage());}
        return lista;
    }

    public String buscarPorTurma(String chave){
        List<Turma> lista=listaTurma();
        String nome;

        for(Turma dp:lista){
            if(chave==dp.getChave()){
                nome=dp.getCurso();
                return nome;
            }
            break;
        }
        return "Turma nao encontrado";
    }

    public int gerarProximoId(){
        int max = 0;
        for(Turma dp : listaTurma()){
            max = Math.max(max, dp.getAno());
        }
        return max + 1;
    }
}
