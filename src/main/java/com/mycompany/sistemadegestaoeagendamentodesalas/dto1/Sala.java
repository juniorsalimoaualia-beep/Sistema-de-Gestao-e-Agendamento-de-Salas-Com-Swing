package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1;

public class Sala{
    private int id;
    private String nome;
    private EstadoSala estado; 

    public Sala(int id, String nome){
        this.id=id;
        this.nome=nome;
    }

    public int getId(){return this.id;}

    public String getNome(){return this.nome;}
    public void setNome(String nome){this.nome=nome;}

    public EstadoSala getEstado(){return this.estado;}

    /*public String toString(){
        return id+"; "+nome+"; "+estado;
    } */
}
