package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1;
public class Curso {
    private int id;
    private String nome;

    public Curso(int id, String nome){
        this.id=id;
        this.nome=nome;
    }

    public int getId(){return this.id;}

    public String getNome(){return this.nome;}
    public void setNome(String nome){this.nome=nome;}

    public String toString(){
        return id+"; "+nome+"";
    }
}
