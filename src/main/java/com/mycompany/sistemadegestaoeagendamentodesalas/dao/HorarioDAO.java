package com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Curso;
import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.DiaSemana;
import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Disciplina;
import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Horario;

public class HorarioDAO {
    private static final String file = "files/Horario.txt";
    private CursoDAO cursoDAO = new CursoDAO();
    private DisciplinaDAO disciplinaDAO = new DisciplinaDAO();

    public void salvar(Horario horario) {
        if (horario == null) {
            System.out.println("Erro: Horario invalido.");
            return;
        }
        if (isHorarioDuplicado(horario)) {
            System.out.println("Erro: Horario duplicado.");
            return;
        }
        File arquivo = ArquivoUtils.prepararArquivo(file);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, true))) {
            bw.write(horario.toString());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Erro ao salvar Horario " + e.getMessage());
        }
    }

    public List<Horario> listaHorario() {
        List<Horario> lista = new ArrayList<>();
        File arquivo = new File(file);
        if (!arquivo.exists()) {
            return lista;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                Horario horario = converterLinha(linha);
                if (horario != null) {
                    lista.add(horario);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o ficheiro " + e.getMessage());
        }
        return lista;
    }

    private Horario converterLinha(String linha) {
        try {
            String[] dados = linha.split("; ");
            if (dados.length < 5) {
                return null;
            }

            int id = Integer.parseInt(dados[0].trim());
            Curso curso;
            Disciplina disciplina = null;
            int indiceDia;

            if (dados.length >= 6 && isInteger(dados[1]) && isInteger(dados[2])) {
                int cursoId = Integer.parseInt(dados[1].trim());
                int disciplinaId = Integer.parseInt(dados[2].trim());

                curso = cursoDAO.buscarCursoPorId(cursoId);
                if (curso == null) {
                    curso = new Curso(cursoId, "");
                }

                if (disciplinaId > 0) {
                    disciplina = disciplinaDAO.buscarPorId(disciplinaId);
                    if (disciplina == null) {
                        disciplina = new Disciplina(disciplinaId, "", null, curso);
                    }
                }
                indiceDia = 3;
            } else {
                curso = new Curso(0, dados[1].trim());
                indiceDia = 2;
            }

            DiaSemana dia = DiaSemana.valueOf(dados[indiceDia].trim());
            return new Horario(
                id,
                curso,
                disciplina,
                dia,
                LocalTime.parse(dados[indiceDia + 1].trim()),
                LocalTime.parse(dados[indiceDia + 2].trim())
            );
        } catch (Exception e) {
            System.out.println("Linha de horario ignorada por estar invalida: " + linha);
            return null;
        }
    }

    private boolean isInteger(String valor) {
        try {
            Integer.parseInt(valor.trim());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String buscarPorHorario(String curso) {
        for (Horario hr : listaHorario()) {
            if (hr.getCurso() != null && curso.equalsIgnoreCase(hr.getCurso().getNome())) {
                return formatarHorario(hr);
            }
        }
        return "Horario nao encontrado";
    }

    public List<Horario> listarPorCurso(String curso) {
        List<Horario> resultado = new ArrayList<>();
        if (curso == null) {
            return resultado;
        }

        for (Horario hr : listaHorario()) {
            if (hr.getCurso() != null && curso.equalsIgnoreCase(hr.getCurso().getNome())) {
                resultado.add(hr);
            }
        }
        return resultado;
    }

    public List<Horario> listarPorDisciplina(int disciplinaId) {
        List<Horario> resultado = new ArrayList<>();
        for (Horario hr : listaHorario()) {
            if (hr.getDisciplina() != null && hr.getDisciplina().getId() == disciplinaId) {
                resultado.add(hr);
            }
        }
        return resultado;
    }

    public Horario buscarPorId(int id) {
        for (Horario hr : listaHorario()) {
            if (hr.getId() == id) {
                return hr;
            }
        }
        return null;
    }

    private String formatarHorario(Horario hr) {
        String nomeCurso = hr.getCurso() != null ? hr.getCurso().getNome() : "";
        String nomeDisciplina = hr.getDisciplina() != null ? hr.getDisciplina().getNome() : "";
        return "Curso: " + nomeCurso
            + " | Disciplina: " + nomeDisciplina
            + " | Dia: " + hr.getDiaSemana()
            + " | Inicio: " + hr.getHoraInicio()
            + " | Fim: " + hr.getHoraFim();
    }

    public int gerarProximoId() {
        int max = 0;
        for (Horario hr : listaHorario()) {
            max = Math.max(max, hr.getId());
        }
        return max + 1;
    }

    private boolean isHorarioDuplicado(Horario novo) {
        if (novo == null) {
            return false;
        }
        for (Horario existente : listaHorario()) {
            boolean mesmoCurso = existente.getCurso() != null && novo.getCurso() != null
                    ? existente.getCurso().getNome().equalsIgnoreCase(novo.getCurso().getNome())
                    : existente.getCurso() == null && novo.getCurso() == null;
            boolean mesmaDisciplina = existente.getDisciplina() != null && novo.getDisciplina() != null
                    ? existente.getDisciplina().getId() == novo.getDisciplina().getId()
                    : existente.getDisciplina() == null && novo.getDisciplina() == null;
            boolean mesmoDia = existente.getDiaSemana() == novo.getDiaSemana();
            boolean mesmoInicio = existente.getHoraInicio() != null && existente.getHoraInicio().equals(novo.getHoraInicio());
            boolean mesmoFim = existente.getHoraFim() != null && existente.getHoraFim().equals(novo.getHoraFim());
            if (mesmoCurso && mesmaDisciplina && mesmoDia && mesmoInicio && mesmoFim) {
                return true;
            }
        }
        return false;
    }
}
