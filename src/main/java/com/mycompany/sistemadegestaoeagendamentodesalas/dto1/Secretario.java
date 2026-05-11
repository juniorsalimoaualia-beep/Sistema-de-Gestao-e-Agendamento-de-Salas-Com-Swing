package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1;
public class Secretario extends Usuario {
    private String cargo;
    public Secretario(int id, String nome, String apelido, String cargo, int numCel, String email, String senha){
        super(id, nome, apelido, numCel, email, senha);
        this.cargo=cargo;
    }

    public String getCargo(){return this.cargo;}
    public void setCargo(String cargo){this.cargo=cargo;}

    public String toString(){
        return super.toString()+"; "+cargo;
    }
}
