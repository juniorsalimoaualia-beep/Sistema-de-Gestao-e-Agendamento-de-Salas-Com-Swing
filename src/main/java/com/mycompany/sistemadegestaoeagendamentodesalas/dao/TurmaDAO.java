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
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Turma;

public class TurmaDAO {
    private static final String file ="files/Turma.txt";
    private EstudanteDAO estudanteDAO = new EstudanteDAO();

    public void salvar(Turma dp){
        File arquivo = ArquivoUtils.prepararArquivo(file);
        try(BufferedWriter bw= new BufferedWriter(new FileWriter(arquivo, true))){
            bw.write(dp.toFileString());
            bw.newLine();
        }catch(IOException e){
            System.out.println("Erro ao salvar Turma "+e.getMessage());
        }
    }

    public List<Turma> listaTurma(){
        List<Turma> lista=new ArrayList<>();
        String linha;
        File arquivo = ArquivoUtils.prepararArquivo(file);
        try(BufferedReader br= new BufferedReader(new FileReader(arquivo))){
            while((linha=br.readLine())!=null){
                String []dados=linha.split("; ");
                if(dados.length>=2){
                    int ano = Integer.parseInt(dados[0]);
                    String cursoNome = dados[1];
                    Turma turma = new Turma(ano, cursoNome, null);
                    if(dados.length == 3 && !dados[2].trim().isEmpty()){
                        String[] ids = dados[2].split(",");
                        for(String id : ids){
                            try{
                                int estudanteId = Integer.parseInt(id.trim());
                                Estudante estudante = estudanteDAO.buscarEstudantePorId(estudanteId);
                                if(estudante != null){
                                    turma.addEstudante(estudante);
                                }
                            }catch(NumberFormatException ignored){ }
                        }
                    }
                    lista.add(turma);
                }
            }
        }catch(IOException e){
            System.out.println("Erro ao ler o ficheiro "+e.getMessage());
        }
        return lista;
    }

    public String buscarPorTurma(String chave){
        List<Turma> lista=listaTurma();
        for(Turma dp:lista){
            if(chave != null && chave.equals(dp.getChave())){
                return dp.getCursoNome();
            }
        }
        return "Turma nao encontrado";
    }

    public boolean turmaExiste(String chave){
        if(chave == null || chave.trim().isEmpty()){
            return false;
        }
        for(Turma dp : listaTurma()){
            if(chave.equals(dp.getChave())){
                return true;
            }
        }
        return false;
    }

    public int gerarProximoId(){
        int max = 0;
        for(Turma dp : listaTurma()){
            max = Math.max(max, dp.getAno());
        }
        return max + 1;
    }
}
