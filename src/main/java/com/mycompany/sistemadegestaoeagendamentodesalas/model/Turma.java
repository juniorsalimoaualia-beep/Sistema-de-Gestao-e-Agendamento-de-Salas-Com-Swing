package com.mycompany.sistemadegestaoeagendamentodesalas.model;

import java.util.ArrayList;
import java.util.List;

public class Turma {
    private int id;
    private String chave;
    private int ano;
    private List<Estudante> estudantes;
    private Curso curso;

    public Turma(int ano, String nomeCurso, List<Estudante> estudantes){
        this(0, ano, new Curso(0, nomeCurso != null ? nomeCurso : ""), estudantes);
    }

    public Turma(int id, int ano, Curso curso, List<Estudante> estudantes){
        this.id = id;
        this.ano = ano;
        this.curso = curso != null ? curso : new Curso(0, "");
        this.chave = ano + "-" + this.curso.getNome();
        this.estudantes = new ArrayList<>();
        if(estudantes != null){
            this.estudantes.addAll(estudantes);
        }
    }

    public int getId(){ return this.id; }
    public int getAno(){ return this.ano; }
    public String getChave(){ return this.chave; }

    public Curso getCurso(){ return this.curso; }
    public String getCursoNome(){ return this.curso != null ? this.curso.getNome() : ""; }

    public List<Estudante> getEstudantes(){ return new ArrayList<>(this.estudantes); }

    public void setEstudantes(List<Estudante> estudantes){
        this.estudantes.clear();
        if(estudantes != null){
            this.estudantes.addAll(estudantes);
        }
    }

    public void addEstudante(Estudante e){
        if(e != null){
            this.estudantes.add(e);
        }
    }

    public void addEstudantes(List<Estudante> estudantes){
        if(estudantes != null){
            for(Estudante e : estudantes){
                if(e != null){
                    this.estudantes.add(e);
                }
            }
        }
    }

    public String getEstudanteIds(){
        String ids = "";
        for (int i = 0; i < estudantes.size(); i++) {
            if (i > 0) {
                ids = ids + ",";
            }
            ids = ids + estudantes.get(i).getId();
        }
        return ids;
    }

    public String toFileString(){
        return ano + "; " + getCursoNome() + "; " + getEstudanteIds();
    }

    @Override
    public String toString(){
        return "Turma " + chave + " - Total estudantes: " + (estudantes != null ? estudantes.size() : 0);
    }
}
