package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1;
public class Disciplina {
    private int id;
    private String nome;
    private Docente docente;
    private String curso;

    public Disciplina(int id, String nome, Docente docente){
        this.id=id;
        this.nome=nome;
        this.docente=docente;
        this.curso="";
    }

    public Disciplina(int id, String nome, Docente docente, String curso){
        this.id=id;
        this.nome=nome;
        this.docente=docente;
        this.curso=curso;
    }

    public int getId(){return this.id;}

    public String getNome(){return this.nome;}
    public void setNome(String nome){this.nome=nome;}

    public Docente getDocente(){return this.docente;}
    public void setDocente(Docente docente){this.docente=docente;}

    public String getCurso(){return this.curso;}
    public void setCurso(String curso){this.curso=curso;}

    public String toString(){
        return id+"; "+nome+"; "+(docente!=null ? docente.getId() : 0)+"; "+curso;
    }
}
