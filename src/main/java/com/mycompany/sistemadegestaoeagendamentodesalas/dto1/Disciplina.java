package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1;
public class Disciplina {
    private int id;
    private String nome;
    private int docenteId;

    public Disciplina(int id, String nome, int docenteId){
        this.id=id;
        this.nome=nome;
        this.docenteId=docenteId;   
    }

    public int getId(){return this.id;}

    public String getNome(){return this.nome;}
    public void setNome(String nome){this.nome=nome;}

    public int getDocenteId(){return this.docenteId;}
    public void setDocenteId(int docenteId){this.docenteId=docenteId;}

    public String toString(){
        return id+"; "+nome+"; "+docenteId;
    }
}
