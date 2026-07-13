// Dados: classe EstudanteDAO

package com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Curso;
import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Estudante;


public class EstudanteDAO {
    private static final String file ="files/estudante.txt";
    private CursoDAO cursoDAO = new CursoDAO();

    public boolean salvar(Estudante dp){
        if (dp == null) {
            System.out.println("Erro: Estudante invalido.");
            return false;
        }
        if (isEstudanteDuplicado(dp)) {
            System.out.println("Erro: Estudante ja cadastrado.");
            return false;
        }
        File arquivo = ArquivoUtils.prepararArquivo(file);
        try(BufferedWriter bw= new BufferedWriter(new FileWriter(arquivo, true))){
            bw.write(dp.toString());
            bw.newLine();
            return true;
        }catch(IOException e){System.out.println("Erro ao salvar Estudante "+e.getMessage());}
        return false;
    }

    public List<Estudante> listaEstudante(){
        List<Estudante> lista=new ArrayList<>();
        String linha;
        File arquivo = ArquivoUtils.prepararArquivo(file);
        try(BufferedReader br= new BufferedReader(new FileReader(arquivo))){
            while((linha=br.readLine())!=null){
                String []dados=linha.split("; ");
                if(dados.length==7){
                    String cursoNome = dados[6];
                    Curso curso = cursoDAO.buscarCursoPorNome(cursoNome);
                    if (curso == null) {
                        curso = new Curso(0, cursoNome);
                    }
                    lista.add(new Estudante(
                        Integer.parseInt(dados[0]),
                        dados[1],
                        dados[2],
                        curso,
                        Integer.parseInt(dados[3]),
                        dados[4],
                        dados[5]));
                }
            }
        }catch(IOException e){System.out.println("Erro ao ler o ficheiro "+e.getMessage());}
        return lista;
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

    public boolean isEstudanteDuplicado(Estudante novo){
        for(Estudante existente : listaEstudante()){
            if (existente.getEmail() != null && existente.getEmail().equalsIgnoreCase(novo.getEmail())){
                return true;
            }
            if (existente.getNomeCompleto() != null && existente.getCursoNome() != null
                && existente.getNomeCompleto().equalsIgnoreCase(novo.getNomeCompleto())
                && existente.getCursoNome().equalsIgnoreCase(novo.getCursoNome())){
                return true;
            }
        }
        return false;
    }

    public boolean alterarSenha(int id, String senhaAtual, String senhaNova) {
        List<Estudante> lista = listaEstudante();
        Estudante estudanteEncontrado = null;
        int indice = -1;

        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == id) {
                estudanteEncontrado = lista.get(i);
                indice = i;
                break;
            }
        }

        if (estudanteEncontrado == null || !estudanteEncontrado.getSenha().equals(senhaAtual)) {
            return false;
        }

        estudanteEncontrado.setSenha(senhaNova);
        lista.set(indice, estudanteEncontrado);
        reescreverArquivo(lista);
        return true;
    }

    public void reescreverArquivo(List<Estudante> lista) {
        File arquivo = ArquivoUtils.prepararArquivo(file);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, false))) {
            for (Estudante es : lista) {
                bw.write(es.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao reescrever arquivo de Estudantes: " + e.getMessage());
        }
    }
}
