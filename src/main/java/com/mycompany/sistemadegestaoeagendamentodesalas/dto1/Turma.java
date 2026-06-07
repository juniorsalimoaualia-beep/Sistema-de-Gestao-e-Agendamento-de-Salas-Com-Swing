package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1;

import java.util.ArrayList;
import java.util.List;

public class Turma {
    private String chave;
    private int ano;
    private List<Estudante> estudantes;
    private String curso;

    public Turma(int ano, String nomeCurso){
        this.ano = ano;
        this.chave = ano + "-" + nomeCurso;
        this.curso = nomeCurso;
        this.estudantes = new ArrayList<>();
    }

    public int getAno(){ return this.ano; }
    public String getChave(){ return this.chave; }

    public String getCurso(){ return this.curso; }

    public List<Estudante> getEstudantes(){ return this.estudantes; }

    public void addEstudante(Estudante e){ if(e!=null) this.estudantes.add(e); }

    @Override
    public String toString(){
        return "Turma " + chave + " - Total estudantes: " + (estudantes != null ? estudantes.size() : 0);
    }
}
