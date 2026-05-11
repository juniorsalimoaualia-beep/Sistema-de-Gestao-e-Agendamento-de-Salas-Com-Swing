package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Estudante;

public class EstudanteDAO {
    private static final String file ="files/estudante.txt";

    public void salvar(Estudante dp){
        File arquivo = ArquivoUtils.prepararArquivo(file);
        try(BufferedWriter bw= new BufferedWriter(new FileWriter(arquivo, true))){
            bw.write(dp.toString());
            bw.newLine();
        }catch(IOException e){System.out.println("Erro ao salvar Estudante "+e.getMessage());}
    }

    public List<Estudante> listaEstudante(){
        List<Estudante> lista=new ArrayList<>();
        String linha;
        File arquivo = ArquivoUtils.prepararArquivo(file);
        try(BufferedReader br= new BufferedReader(new FileReader(arquivo))){
            while((linha=br.readLine())!=null){
                String []dados=linha.split("; ");
                if(dados.length==6){
                    String []nomeParts = dividirNomeCompleto(dados[1]);
                    lista.add(new Estudante(
                        Integer.parseInt(dados[0]),
                        nomeParts[0],
                        nomeParts[1],
                        dados[5],
                        Integer.parseInt(dados[2]),
                        dados[3],
                        dados[4]));
                } else if(dados.length==7){
                    lista.add(new Estudante(
                        Integer.parseInt(dados[0]),
                        dados[1],
                        dados[2],
                        dados[6],
                        Integer.parseInt(dados[3]),
                        dados[4],
                        dados[5]));
                }
            }
        }catch(IOException e){System.out.println("Erro ao ler o ficheiro "+e.getMessage());}
        return lista;
    }

    private String[] dividirNomeCompleto(String nomeCompleto){
        String nome = nomeCompleto;
        String apelido = "";
        String[] partes = nomeCompleto.split(" ", 2);
        if(partes.length == 2){
            nome = partes[0];
            apelido = partes[1];
        }
        return new String[]{nome, apelido};
    }

    public Estudante buscarEstudantePorId(int id){
        List<Estudante> lista = listaEstudante();
        for(Estudante dp : lista){
            if(id == dp.getId()){
                return dp;
            }
        }
        return null;
    }

    public String buscarPorEstudante(int id){
        List<Estudante> lista=listaEstudante();
        String nome;

        for(Estudante dp:lista){
            if(id==dp.getId()){
                nome=dp.getNome();
                return nome;
            }
        }
        return "Estudante nao encontrado";
    }

    public int gerarProximoId(){
        int max = 0;
        for(Estudante dp : listaEstudante()){
            max = Math.max(max, dp.getId());
        }
        return max + 1;
    }
}
