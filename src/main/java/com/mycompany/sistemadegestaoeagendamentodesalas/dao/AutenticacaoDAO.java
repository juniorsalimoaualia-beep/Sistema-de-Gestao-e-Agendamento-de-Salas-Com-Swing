package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Docente;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Estudante;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Secretario;

public class AutenticacaoDAO {
    private static final String ADMIN_FILE = "files/admin.txt";
    private DocenteDAO dcDao = new DocenteDAO();
    private EstudanteDAO esDao = new EstudanteDAO();
    private SecretarioDAO secretDao = new SecretarioDAO();
    
   
    public boolean autenticarLogin(String perfil, String email, String senha){
        if("Admin".equalsIgnoreCase(perfil)){
            return autenticarAdmin(email, senha);
        }

        if("Docente".equalsIgnoreCase(perfil)){
            return autenticarDocente(email, senha);
        }

        if("Estudante".equalsIgnoreCase(perfil)){
            return autenticarEstudante(email, senha);
        }

        if("Secretario".equalsIgnoreCase(perfil)){
            return autenticarSecretario(email, senha);
        }

        System.out.println("Perfil invalido ou nao suportado.");
        return false;
    }

    public boolean autenticarAdmin(String email, String senha){
        File arquivo = ArquivoUtils.prepararArquivo(ADMIN_FILE);
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";\\s*");
                if (dados.length >= 2 && email.equals(dados[0]) && senha.equals(dados[1])) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o ficheiro de admin: " + e.getMessage());
        }
        return false;
    }

    public boolean autenticarDocente(String email, String senha){
        List<Docente> lista = dcDao.listaDocente();
        for (Docente dc : lista){
            if(email.equals(dc.getEmail()) && senha.equals(dc.getSenha())){
                return true;
            }
        }
        return false;
    }

    public boolean autenticarEstudante(String email, String senha){
        List<Estudante> lista = esDao.listaEstudante();
        for (Estudante es : lista){
            if(email.equals(es.getEmail()) && senha.equals(es.getSenha())){
                return true;
            }
        }
        return false;
    }

    public boolean autenticarSecretario(String email, String senha){
        List<Secretario> lista = secretDao.listaSecretario();
        for (Secretario sec : lista){
            if(email.equals(sec.getEmail()) && senha.equals(sec.getSenha())){
                return true;
            }
        }
        return false;
    }
}

