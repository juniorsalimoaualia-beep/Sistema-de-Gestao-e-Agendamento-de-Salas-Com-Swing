package com.mycompany.sistemadegestaoeagendamentodesalas.model;

public class Departamento {
    private int id;
    private String nome;
    private Curso curso;
    private Sala sala;
    private Secretario secretario;
    
    public Departamento(int id, String nome){
        this(id, nome, null, null);
    }

    public Departamento(int id, String nome, Curso curso, Sala sala){
        this(id, nome, curso, sala, null);
    }

    public Departamento(int id, String nome, Curso curso, Sala sala, Secretario secretario){
        this.id = id;
        this.nome = nome;
        this.curso = curso;
        this.sala = sala;
        this.secretario = secretario;
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

    public Secretario getSecretario(){
        return secretario;
    }

    public void setSecretario(Secretario secretario){
        this.secretario = secretario;
    }

    @Override
    public String toString(){
        int cursoId = curso != null ? curso.getId() : -1;
        int salaId = sala != null ? sala.getId() : -1;
        return id + "; " + nome + "; " + cursoId + "; " + salaId;
    }
}
