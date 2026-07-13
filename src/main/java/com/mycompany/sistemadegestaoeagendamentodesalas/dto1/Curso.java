// Dados: classe Curso

package com.mycompany.sistemadegestaoeagendamentodesalas.dto1;

public class Curso {
    private int id;
    private String nome;
    private Departamento departamento;

    public Curso(int id, String nome) {
        this(id, nome, null);
    }

    public Curso(int id, String nome, Departamento departamento) {
        this.id = id;
        this.nome = nome;
        this.departamento = departamento;
    }

    public int getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Departamento getDepartamento() {
        return this.departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    @Override
    public String toString() {
        return id + "; " + nome + (departamento != null ? "; " + departamento.getId() : "");
    }
}
