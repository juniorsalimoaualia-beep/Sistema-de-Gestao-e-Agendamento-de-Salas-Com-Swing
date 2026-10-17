package com.mycompany.sistemadegestaoeagendamentodesalas.model;
public class Estudante extends Usuario{
    private Curso curso;
    private int ano;

    public Estudante(int id, String nome, String apelido, Curso curso, int numCel, String email, String senha){
        this(id, nome, apelido, curso, 0, numCel, email, senha);
    }

    public Estudante(int id, String nome, String apelido, Curso curso, int ano, int numCel, String email, String senha){
        super(id,nome,apelido,numCel,email, senha);
        this.curso = curso != null ? curso : new Curso(0, "");
        this.ano = ano;
    }

    public Estudante(int id, String nome, String apelido, String cursoNome, int numCel, String email, String senha){
        this(id, nome, apelido, new Curso(0, cursoNome), numCel, email, senha);
    }

    public Estudante(int id, String nome, String apelido, String cursoNome, int ano, int numCel, String email, String senha){
        this(id, nome, apelido, new Curso(0, cursoNome), ano, numCel, email, senha);
    }

    public Curso getCurso(){return this.curso;}
    public void setCurso(Curso curso){this.curso = curso != null ? curso : new Curso(0, "");}
    public void setCurso(String cursoNome){
        this.curso = new Curso(0, cursoNome != null ? cursoNome : "");
    }

    public int getAno(){return this.ano;}
    public void setAno(int ano){this.ano = ano;}

    public String getCursoNome(){
        return curso != null ? curso.getNome() : "";
    }
    
    public String toString(){
        return super.toString()+"; "+getCursoNome()+"; "+ano;
    }
}
