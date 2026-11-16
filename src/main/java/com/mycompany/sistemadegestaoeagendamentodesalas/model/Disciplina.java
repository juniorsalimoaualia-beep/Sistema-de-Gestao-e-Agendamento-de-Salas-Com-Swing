package com.mycompany.sistemadegestaoeagendamentodesalas.model;
public class Disciplina {
    private int id;
    private String nome;
    private Docente docente;
    private Curso curso;
    private int ano;

    public Disciplina(int id, String nome, Docente docente){
        this(id, nome, docente, new Curso(0, ""), 1);
    }

    public Disciplina(int id, String nome, Docente docente, Curso curso){
        this(id, nome, docente, curso, 1);
    }

    public Disciplina(int id, String nome, Docente docente, Curso curso, int ano){
        this.id=id;
        this.nome=nome;
        this.docente=docente;
        this.curso = curso != null ? curso : new Curso(0, "");
        setAno(ano);
    }

    public int getId(){return this.id;}

    public String getNome(){return this.nome;}
    public void setNome(String nome){this.nome=nome;}

    public Docente getDocente(){return this.docente;}
    public void setDocente(Docente docente){this.docente=docente;}

    public Curso getCurso(){return this.curso;}
    public void setCurso(Curso curso){this.curso = curso != null ? curso : new Curso(0, "");}
    public void setCurso(String cursoNome){
        this.curso = new Curso(0, cursoNome != null ? cursoNome : "");
    }

    public String getCursoNome(){
        return curso != null ? curso.getNome() : "";
    }

    public int getAno(){return this.ano;}
    public void setAno(int ano){
        if (ano < 1) throw new IllegalArgumentException("O ano da disciplina deve ser positivo.");
        this.ano = ano;
    }

    public String toString(){
        return id+"; "+nome+"; "+(docente!=null ? docente.getId() : 0)+"; "+getCursoNome()+"; "+ano;
    }
}
