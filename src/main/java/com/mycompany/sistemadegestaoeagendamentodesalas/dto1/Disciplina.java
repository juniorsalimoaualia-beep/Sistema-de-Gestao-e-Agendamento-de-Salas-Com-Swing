package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1;
public class Disciplina {
    private int id;
    private String nome;
    private Docente docente;
    private Curso curso;

    public Disciplina(int id, String nome, Docente docente){
        this(id, nome, docente, new Curso(0, ""));
    }

    public Disciplina(int id, String nome, Docente docente, Curso curso){
        this.id=id;
        this.nome=nome;
        this.docente=docente;
        this.curso = curso != null ? curso : new Curso(0, "");
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

    public String toString(){
        return id+"; "+nome+"; "+(docente!=null ? docente.getId() : 0)+"; "+getCursoNome();
    }
}
