package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Secretario;

public class SecretarioDAO {
    private static final String file ="files/Secretario.txt";

    public void salvar(Secretario dp){
        File arquivo = ArquivoUtils.prepararArquivo(file);
        try(BufferedWriter bw= new BufferedWriter(new FileWriter(arquivo, true))){
            bw.write(dp.toString());
            bw.newLine();
        }catch(IOException e){System.out.println("Erro ao salvar Secretario "+e.getMessage());}
    }

    public List<Secretario> listaSecretario(){
        List<Secretario> lista=new ArrayList<>();
        String linha;
        File arquivo = ArquivoUtils.prepararArquivo(file);
        try(BufferedReader br= new BufferedReader(new FileReader(arquivo))){
            while((linha=br.readLine())!=null){
                String []dados=linha.split("; ");
                if(dados.length==6){
                    String []nomeParts = dividirNomeCompleto(dados[1]);
                    lista.add(new Secretario(
                        Integer.parseInt(dados[0]),
                        nomeParts[0],
                        nomeParts[1],
                        dados[5],
                        Integer.parseInt(dados[2]),
                        dados[3],
                        dados[4]
                    ));
                } else if(dados.length==7){
                    lista.add(new Secretario(
                        Integer.parseInt(dados[0]),
                        dados[1],
                        dados[2],
                        dados[3],
                        Integer.parseInt(dados[4]),
                        dados[5],
                        dados[6]
                    ));
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

    public Secretario buscarPorSecretario(int id){
        List<Secretario> lista=listaSecretario();

        for(Secretario sc:lista){
            if(id==sc.getId()){
                return sc;
            }
        }
        return null;
    }

    public int gerarProximoId(){
        int max = 0;
        for(Secretario dp : listaSecretario()){
            max = Math.max(max, dp.getId());
        }
        return max + 1;
    }

    public boolean alterarSenha(int id, String senhaAtual, String senhaNova) {
        List<Secretario> lista = listaSecretario();
        Secretario secretarioEncontrado = null;
        int indice = -1;

        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == id) {
                secretarioEncontrado = lista.get(i);
                indice = i;
                break;
            }
        }

        if (secretarioEncontrado == null || !secretarioEncontrado.getSenha().equals(senhaAtual)) {
            return false;
        }

        secretarioEncontrado.setSenha(senhaNova);
        lista.set(indice, secretarioEncontrado);
        reescreverArquivo(lista);
        return true;
    }

    public void reescreverArquivo(List<Secretario> lista) {
        File arquivo = ArquivoUtils.prepararArquivo(file);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, false))) {
            for (Secretario sc : lista) {
                bw.write(sc.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao reescrever arquivo de Secretários: " + e.getMessage());
        }
    }
}
