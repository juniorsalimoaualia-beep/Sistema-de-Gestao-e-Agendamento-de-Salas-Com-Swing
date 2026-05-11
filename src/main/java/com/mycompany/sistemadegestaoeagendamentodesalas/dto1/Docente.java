package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1;

public class Docente extends Usuario{
    private String nivelAcademico;
    public Docente(int id, String nome, String apelido, String nivel, int numCel, String email, String senha){
        super(id,nome,apelido,numCel,email, senha);
        this.nivelAcademico=nivel;
    }
    public String getNivelAcademico(){return nivelAcademico;}
    public void setNivelAcademico(String nivel){this.nivelAcademico=nivel;}
    
    @Override
    public String toString(){
        return super.toString()+"; "+nivelAcademico;
    }
}
