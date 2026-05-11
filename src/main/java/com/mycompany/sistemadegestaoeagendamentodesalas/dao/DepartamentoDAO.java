package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Departamento;
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
                if(dados.length==2){
                    lista.add(new Departamento(Integer.parseInt(dados[0]), dados[1]));
                }
            }
        }catch(IOException e){System.out.println("Erro ao ler o ficheiro "+e.getMessage());}
        return lista;
    }

    public String buscarPorDepartamento(int id){
        List<Departamento> lista=listaDepartamento();
        String nome;

        for(Departamento dp:lista){
            if(id==dp.getId()){
                nome=dp.getNome();
                return nome;
            }
        }
        return "Departamento nao encontrado";
    }

    public int gerarProximoId(){
        int max = 0;
        for(Departamento dp : listaDepartamento()){
            max = Math.max(max, dp.getId());
        }
        return max + 1;
    }
}
