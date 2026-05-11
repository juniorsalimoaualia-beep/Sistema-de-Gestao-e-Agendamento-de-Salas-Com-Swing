package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1;

public class Departamento {
    private int id;
    private String nome;
    private Curso curso;
    private Sala sala;
    
    public Departamento(int id, String nome){
        this.id=id;
        this.nome=nome;
    }

    public int getId(){return id;}

    public String getNome(){return nome;}
    public void setNome(String nome){this.nome=nome;}

    public String getCurso(){ return curso != null ? curso.getNome() : ""; }
    public String getSala(){ return sala != null ? sala.getNome() : ""; }

    public String toString(){
        return id + "; " + nome;
    }
}
