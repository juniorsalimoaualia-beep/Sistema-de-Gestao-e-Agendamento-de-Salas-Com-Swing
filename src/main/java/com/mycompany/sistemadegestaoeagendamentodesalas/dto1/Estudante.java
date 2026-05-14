package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1;
public class Estudante extends Usuario{
    private String curso;
    public Estudante(int id, String nome, String apelido, String curso, int numCel, String email, String senha){
        super(id,nome,apelido,numCel,email, senha);
        this.curso=curso;
    }

    public String getCurso(){return this.curso;}
    public void setCurso(String curso){this.curso=curso;}
    
    public String toString(){
        return super.toString()+"; "+curso;
    }
}
