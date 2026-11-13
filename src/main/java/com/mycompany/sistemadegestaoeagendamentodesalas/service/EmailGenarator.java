package com.mycompany.sistemadegestaoeagendamentodesalas.service;

import java.util.Random;
import javax.swing.JTextField;

public class EmailGenarator {
    private static final String dominio="@uem.com";

    public String gerarEmail(JTextField name, JTextField surname){

        String nome= name.getText().trim();
        String apelido=surname.getText().trim();
        String nomeCompleto=nome+apelido;
        int numero= new Random().nextInt(999);
        return nomeCompleto.toLowerCase()+numero+dominio;
    }
}
