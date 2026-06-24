package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1;
public class Estudante extends Usuario{
    private Curso curso;

    public Estudante(int id, String nome, String apelido, Curso curso, int numCel, String email, String senha){
        super(id,nome,apelido,numCel,email, senha);
        this.curso = curso != null ? curso : new Curso(0, "");
    }

    public Estudante(int id, String nome, String apelido, String cursoNome, int numCel, String email, String senha){
        this(id, nome, apelido, new Curso(0, cursoNome), numCel, email, senha);
    }

    public Curso getCurso(){return this.curso;}
    public void setCurso(Curso curso){this.curso = curso != null ? curso : new Curso(0, "");}
    public void setCurso(String cursoNome){
        this.curso = new Curso(0, cursoNome != null ? cursoNome : "");
    }

    public String getCursoNome(){
        return curso != null ? curso.getNome() : "";
    }
    
    public String toString(){
        return super.toString()+"; "+getCursoNome();
    }
}
