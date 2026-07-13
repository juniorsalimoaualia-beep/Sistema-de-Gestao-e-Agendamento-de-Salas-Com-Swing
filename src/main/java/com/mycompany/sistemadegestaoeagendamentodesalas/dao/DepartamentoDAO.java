// Dados: classe DepartamentoDAO

package com.mycompany.sistemadegestaoeagendamentodesalas.dao;
import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Departamento;
import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Curso;
import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Sala;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.List;
import java.util.ArrayList;


public class DepartamentoDAO {
    private static final String file ="files/departamento.txt";

    public void salvar(Departamento dp){
        File arquivo = ArquivoUtils.prepararArquivo(file);
        try(BufferedWriter bw= new BufferedWriter(new FileWriter(arquivo, true))){
            bw.write(dp.toString());
            bw.newLine();
        }catch(IOException e){System.out.println("Erro ao salvar departamento "+e.getMessage());}
    }

    public List<Departamento> listaDepartamento(){
        List<Departamento> lista=new ArrayList<>();
        String linha;
        File arquivo = new File(file);
        if (!arquivo.exists()) {
            return lista;
        }
        try(BufferedReader br= new BufferedReader(new FileReader(arquivo))){
            while((linha=br.readLine())!=null){
                String []dados=linha.split("; ");
                if(dados.length >= 2){
                    Departamento departamento = new Departamento(Integer.parseInt(dados[0]), dados[1]);
                    if (dados.length >= 4) {
                        int cursoId = Integer.parseInt(dados[2]);
                        int salaId = Integer.parseInt(dados[3]);
                        departamento.setCurso(new Curso(cursoId, ""));
                        departamento.setSala(new Sala(salaId, ""));
                    }
                    lista.add(departamento);
                }
            }
        }catch(IOException e){System.out.println("Erro ao ler o ficheiro "+e.getMessage());}
        return lista;
    }

    public Departamento buscarPorId(int id){
        List<Departamento> lista = listaDepartamento();
        for (Departamento dp : lista) {
            if (id == dp.getId()) {
                return dp;
            }
        }
        return null;
    }

    public String buscarPorDepartamento(int id){
        Departamento departamento = buscarPorId(id);
        return departamento != null ? departamento.getNome() : "Departamento nao encontrado";
    }

    public int gerarProximoId(){
        int max = 0;
        for(Departamento dp : listaDepartamento()){
            max = Math.max(max, dp.getId());
        }
        return max + 1;
    }
}
