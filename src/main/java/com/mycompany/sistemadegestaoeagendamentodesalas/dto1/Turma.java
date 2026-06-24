package com.mycompany.sistemadegestaoeagendamentodesalas.dto1;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Turma {
    private String chave;
    private int ano;
    private List<Estudante> estudantes;
    private Curso curso;

    public Turma(int ano, String nomeCurso, List<Estudante> estudantes){
        this.ano = ano;
        this.curso = new Curso(0, nomeCurso != null ? nomeCurso : "");
        this.chave = ano + "-" + this.curso.getNome();
        this.estudantes = new ArrayList<>();
        if(estudantes != null){
            this.estudantes.addAll(estudantes);
        }
    }

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
        return estudantes.stream()
                .map(es -> String.valueOf(es.getId()))
                .collect(Collectors.joining(","));
    }

    public String toFileString(){
        return ano + "; " + getCursoNome() + "; " + getEstudanteIds();
    }

    @Override
    public String toString(){
        return "Turma " + chave + " - Total estudantes: " + (estudantes != null ? estudantes.size() : 0);
    }
}
