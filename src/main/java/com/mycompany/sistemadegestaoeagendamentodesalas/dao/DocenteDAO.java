package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Docente;

public class DocenteDAO {
    private static final String file ="files/Docente.txt";

    public void salvar(Docente dc){
        File arquivo = ArquivoUtils.prepararArquivo(file);
        try(BufferedWriter bw= new BufferedWriter(new FileWriter(arquivo, true))){
            bw.write(dc.toString());
            bw.newLine();
        }catch(IOException e){System.out.println("Erro ao salvar Docente "+e.getMessage());}
    }

    public List<Docente> listaDocente(){
        List<Docente> lista=new ArrayList<>();
        String linha;
        File arquivo = ArquivoUtils.prepararArquivo(file);
        try(BufferedReader br= new BufferedReader(new FileReader(arquivo))){
            while((linha=br.readLine())!=null){
                String []dados=linha.split("; ");
                if(dados.length==6){
                    String []nomeParts = dividirNomeCompleto(dados[1]);
                    lista.add(new Docente(
                        Integer.parseInt(dados[0]),
                        nomeParts[0],
                        nomeParts[1],
                        dados[5],
                        Integer.parseInt(dados[2]),
                        dados[3],
                        dados[4]));
                } else if(dados.length==7){
                    lista.add(new Docente(
                        Integer.parseInt(dados[0]),
                        dados[1],
                        dados[2],
                        dados[3],
                        Integer.parseInt(dados[4]),
                        dados[5],
                        dados[6]));
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

    public boolean autenticarDocente(String email, String senha){
        List<Docente> lista=listaDocente();
        for(Docente dc: lista){
            if(email.equals(dc.getEmail())&&senha.equals(dc.getSenha())){
                return true;
            }
        }
        return false;
    }

    public Docente buscarPorDocente(int id){
        List<Docente> lista = listaDocente();

        for(Docente dc : lista){
            if(id == dc.getId()){
                return dc;
            }
        }
        return null;
    }

    public int gerarProximoId(){
        int max = 0;
        for(Docente dc : listaDocente()){
            max = Math.max(max, dc.getId());
        }
        return max + 1;
    }

    public boolean alterarSenha(int id, String senhaAtual, String senhaNova) {
        List<Docente> lista = listaDocente();
        Docente docenteEncontrado = null;
        int indice = -1;

        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == id) {
                docenteEncontrado = lista.get(i);
                indice = i;
                break;
            }
        }

        if (docenteEncontrado == null || !docenteEncontrado.getSenha().equals(senhaAtual)) {
            return false;
        }

        docenteEncontrado.setSenha(senhaNova);
        lista.set(indice, docenteEncontrado);
        reescreverArquivo(lista);
        return true;
    }

    public void reescreverArquivo(List<Docente> lista) {
        File arquivo = ArquivoUtils.prepararArquivo(file);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, false))) {
            for (Docente dc : lista) {
                bw.write(dc.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao reescrever arquivo de Docentes: " + e.getMessage());
        }
    }
}
