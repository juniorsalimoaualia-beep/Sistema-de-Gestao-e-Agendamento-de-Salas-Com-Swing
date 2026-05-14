package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Inscricao;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Estudante;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Disciplina;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InscricaoDAO {
    private static final String file = "files/inscricao.txt";
    private EstudanteDAO estudanteDAO = new EstudanteDAO();
    private DisciplinaDAO disciplinaDAO = new DisciplinaDAO();

    public void salvar(Inscricao inscricao) {
        File arquivo = ArquivoUtils.prepararArquivo(file);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, true))) {
            bw.write(inscricao.toString());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Erro ao salvar inscricao " + e.getMessage());
        }
    }

    public List<Inscricao> listaInscricao() {
        List<Inscricao> lista = new ArrayList<>();
        String linha;
        File arquivo = ArquivoUtils.prepararArquivo(file);

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split("; ");
                if (dados.length >= 3) {
                    int id = Integer.parseInt(dados[0].trim());
                    int estudanteId = Integer.parseInt(dados[1].trim());
                    int disciplinaId = Integer.parseInt(dados[2].trim());
                    Estudante estudante = estudanteDAO.buscarEstudantePorId(estudanteId);
                    Disciplina disciplina = disciplinaDAO.buscarPorId(disciplinaId);
                    if (estudante == null) {
                        estudante = new Estudante(estudanteId, "", "", "", 0, "", "");
                    }
                    if (disciplina == null) {
                        disciplina = new Disciplina(disciplinaId, "", 0);
                    }
                    lista.add(new Inscricao(id, estudante, disciplina));
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao listar inscricoes " + e.getMessage());
        }
        return lista;
    }

    public Inscricao buscarPorInscricao(int id) {
        List<Inscricao> lista = listaInscricao();
        for (Inscricao inscricao : lista) {
            if (inscricao.getId() == id) {
                return inscricao;
            }
        }
        return null;
    }

    public List<Inscricao> buscarPorEstudante(int estudanteId) {
        List<Inscricao> lista = listaInscricao();
        List<Inscricao> resultado = new ArrayList<>();
        for (Inscricao inscricao : lista) {
            if (inscricao.getEstudante() != null && inscricao.getEstudante().getId() == estudanteId) {
                resultado.add(inscricao);
            }
        }
        return resultado;
    }

    public List<Inscricao> buscarPorDisciplina(int disciplinaId) {
        List<Inscricao> lista = listaInscricao();
        List<Inscricao> resultado = new ArrayList<>();
        for (Inscricao inscricao : lista) {
            if (inscricao.getDisciplina() != null && inscricao.getDisciplina().getId() == disciplinaId) {
                resultado.add(inscricao);
            }
        }
        return resultado;
    }

    public int gerarProximoId(){
        int max = 0;
        for(Inscricao inscricao : listaInscricao()){
            max = Math.max(max, inscricao.getId());
        }
        return max + 1;
    }
}
