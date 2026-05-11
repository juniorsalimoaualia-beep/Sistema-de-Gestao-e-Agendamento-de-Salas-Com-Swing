package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1;
public class Disciplina {
    private int id;
    private String nome;
    private String docente;

    public Disciplina(int id, String nome, String docente){
        this.id=id;
        this.nome=nome;
        this.docente=docente;   
    }

    public int getId(){return this.id;}

    public String getNome(){return this.nome;}
    public void setNome(String nome){this.nome=nome;}

    public String getDocente(){return this.docente;}
    public void setDocente(String docente){this.docente=docente;}

    public String toString(){
        return id+"; "+nome+"; "+docente;
    }
}
