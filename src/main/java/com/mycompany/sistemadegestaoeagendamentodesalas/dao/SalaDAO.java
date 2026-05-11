package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Sala;

public class SalaDAO {
    private static final String file ="files/Sala.txt";

    public void salvar(Sala sala){
        try(BufferedWriter bw= new BufferedWriter(new FileWriter(file, true))){
            bw.write(sala.toString());
            bw.newLine();
        }catch(IOException e){System.out.println("Erro ao salvar Sala "+e.getMessage());}
    }

    public List<Sala> listaSala(){
        List<Sala> lista=new ArrayList<>();
        String linha;
        try(BufferedReader br= new BufferedReader(new FileReader(file))){
            while((linha=br.readLine())!=null){
                String []dados=linha.split("; ");
                if(dados.length==2){
                    lista.add(new Sala(Integer.parseInt(dados[0]), dados[1]));
                }
            }
        }catch(IOException e){System.out.println("Erro ao ler o ficheiro "+e.getMessage());}
        return lista;
    }

    public Sala buscarPorSala(String nome){
        List<Sala> lista = listaSala();

        for(Sala sala : lista){
            if(nome != null && nome.equalsIgnoreCase(sala.getNome())){
                return sala;
            }
        }
        return null;
    }

    public int gerarProximoId(){
        int max = 0;
        for(Sala sala : listaSala()){
            max = Math.max(max, sala.getId());
        }
        return max + 1;
    }
}
