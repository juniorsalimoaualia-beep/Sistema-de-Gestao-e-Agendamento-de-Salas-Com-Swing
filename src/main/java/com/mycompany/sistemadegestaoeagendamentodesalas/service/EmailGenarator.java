package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.service;

import java.util.Random;

public class EmailGenarator {
    private static final String dominio="@uem.com";

    public String gerarEmail(String nome, String apelido){
        String nomeCompleto= nome+apelido;
        int numero= new Random().nextInt(999);
        return nomeCompleto.toLowerCase()+numero+dominio;
    }
}
