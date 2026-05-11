package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import java.io.File;
import java.io.IOException;

public class ArquivoUtils {
    public static File prepararArquivo(String caminhoArquivo) {
        File arquivo = new File(caminhoArquivo);
        File pasta = arquivo.getParentFile();
        if (pasta != null && !pasta.exists()) {
            pasta.mkdirs();
        }
        try {
            if (!arquivo.exists()) {
                arquivo.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Erro ao preparar ficheiro: " + e.getMessage());
        }
        return arquivo;
    }
}
