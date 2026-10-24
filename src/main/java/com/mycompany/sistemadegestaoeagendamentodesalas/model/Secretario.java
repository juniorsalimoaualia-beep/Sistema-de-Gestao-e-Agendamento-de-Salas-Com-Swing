package com.mycompany.sistemadegestaoeagendamentodesalas.model;
public class Secretario extends Usuario {
    private String cargo;
    private Departamento departamento;
    public Secretario(int id, String nome, String apelido, String cargo, int numCel, String email, String senha){
        super(id, nome, apelido, numCel, email, senha);
        this.cargo=cargo;
    }

    public String getCargo(){return this.cargo;}
    public void setCargo(String cargo){this.cargo=cargo;}

    public Departamento getDepartamento(){return departamento;}
    public void setDepartamento(Departamento departamento){this.departamento=departamento;}

    public String toString(){
        return super.toString()+"; "+cargo;
    }
}
