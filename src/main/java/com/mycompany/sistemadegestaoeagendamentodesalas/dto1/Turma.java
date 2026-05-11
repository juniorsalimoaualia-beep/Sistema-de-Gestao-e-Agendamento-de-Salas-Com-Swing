package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1;

public class Turma {
    private String chave;
    private int ano;
    private Estudante estudantes;
    private String curso;


    public Turma(int ano, String nomeCurso){
        
        this.ano=ano;
        this.chave=ano+"-"+nomeCurso;
        
    }
   
    public int getAno(){return this.ano;}
    public String getChave(){return this.chave;}

    public String getCurso(){return this.curso;}

    public Estudante getEstudantes(){return this.estudantes;}
    
    public String toString(){
        return "Turma "+chave+"- Total: "+estudantes.getNomeCompleto();}
}
