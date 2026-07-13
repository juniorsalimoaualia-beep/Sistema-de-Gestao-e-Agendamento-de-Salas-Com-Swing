package com.mycompany.sistemadegestaoeagendamentodesalas.dto1;

public class Departamento {
    private int id;
    private String nome;
    private Curso curso;
    private Sala sala;
    
    public Departamento(int id, String nome){
        this(id, nome, null, null);
    }

    public Departamento(int id, String nome, Curso curso, Sala sala){
        this.id = id;
        this.nome = nome;
        this.curso = curso;
        this.sala = sala;
    }

    public int getId(){return id;}

    public String getNome(){return nome;}
    public void setNome(String nome){this.nome=nome;}

    public Curso getCurso(){
        return curso;
    }

    public void setCurso(Curso curso){
        this.curso = curso;
    }

    public Sala getSala(){
        return sala;
    }

    public void setSala(Sala sala){
        this.sala = sala;
    }

    @Override
    public String toString(){
        int cursoId = curso != null ? curso.getId() : -1;
        int salaId = sala != null ? sala.getId() : -1;
        return id + "; " + nome + "; " + cursoId + "; " + salaId;
    }
}
