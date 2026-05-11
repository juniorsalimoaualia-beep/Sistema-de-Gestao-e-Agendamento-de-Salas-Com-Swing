package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1;

public class Inscricao {
    private int id;
    private Estudante estudante;
    private Disciplina disciplina;

    public Inscricao(int id, Estudante estudante, Disciplina disciplina) {
        this.id = id;
        this.estudante = estudante;
        this.disciplina = disciplina;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Estudante getEstudante() {
        return estudante;
    }

    public void setEstudante(Estudante estudante) {
        this.estudante = estudante;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    @Override
    public String toString() {
        return id + "; " + (estudante != null ? estudante.getId() : 0) + "; " + (disciplina != null ? disciplina.getId() : 0);
    }
}
