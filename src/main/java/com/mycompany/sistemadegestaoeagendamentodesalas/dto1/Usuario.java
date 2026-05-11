package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1;

public class Usuario {
    protected int id;
    protected String nome;
    protected String apelido;
    protected String nomeCompleto;
    protected int numCel;
    protected String email;
    protected String senha;
    public Usuario(int id, String nome, String apelido, int num,String email, String senha){
        this.id=id;
        this.nome=nome;
        this.apelido=apelido;
        this.nomeCompleto= nome+" "+apelido;
        this.numCel=num;
        this.email=email;
        this.senha=senha;
    }
    
    public int getId(){return this.id;}

    public String getNome(){return this.nome;}
    public void setNome(String nome){this.nome=nome;}
    
    public String getApelido(){return this.apelido;}
    public void setApelido(String apel){this.apelido=apel;}

    public String getNomeCompleto(){return this.nomeCompleto;}
    
    public int getNumCel(){return this.numCel;}
    public void setNumCel(int num){this.numCel=num;}
    
    public String getEmail(){return this.email;}
    public void setEmail(String em){this.email=em;}

    public String getSenha(){return this.senha;}
    public void setSenha(String senha){this.senha=senha;}
    @Override
    public String toString(){
        return id+"; "+nomeCompleto+"; "+numCel+"; "+email+"; "+senha;
    }
}
