package com.mycompany.sistemadegestaoeagendamentodesalas.model;
import java.time.LocalTime;
public class Horario {
    private int id;
    private Curso curso;
    private Disciplina disciplina;
    private Sala sala;
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFim;

    public Horario(int id, Curso curso, DiaSemana dia, LocalTime horaInicio, LocalTime horaFim){
        this(id, curso, null, dia, horaInicio, horaFim);
    }

    public Horario(int id, String nomeCurso, DiaSemana dia, LocalTime horaInicio, LocalTime horaFim){
        this(id, new Curso(0, nomeCurso), null, dia, horaInicio, horaFim);
    }

    public Horario(int id, Curso curso, Disciplina disciplina, DiaSemana dia, LocalTime horaInicio, LocalTime horaFim){
        this(id, curso, disciplina, null, dia, horaInicio, horaFim);
    }

    public Horario(int id, Curso curso, Disciplina disciplina, Sala sala, DiaSemana dia,
            LocalTime horaInicio, LocalTime horaFim){
        this.id=id;
        this.curso=curso;
        this.disciplina=disciplina;
        this.sala=sala;
        this.diaSemana=dia;
        this.horaInicio=horaInicio;
        this.horaFim=horaFim;
    }

    public int getId(){return this.id;}

    public Curso getCurso(){return this.curso;}
    public void setCurso(Curso curso){this.curso=curso;}

    public String getNomeCurso(){return curso!=null ? curso.getNome() : "";}
    public void setNome(String nome){this.curso=new Curso(curso!=null ? curso.getId() : 0, nome);}

    public Disciplina getDisciplina(){return this.disciplina;}
    public void setDisciplina(Disciplina disciplina){this.disciplina=disciplina;}

    public Sala getSala(){return this.sala;}
    public void setSala(Sala sala){this.sala=sala;}

    public DiaSemana getDiaSemana(){return diaSemana;}
    public void setDiaSemana(DiaSemana dia){this.diaSemana=dia;}

    public LocalTime getHoraInicio(){return this.horaInicio;}
    public void setHoraInicio(LocalTime horaIni){this.horaInicio=horaIni;}

    public LocalTime getHoraFim(){return horaFim;}
    public void setHoraFim(LocalTime horaFim){this.horaFim=horaFim;}

    public String toString(){
        return id+"; "+(curso!=null ? curso.getId() : 0)+"; "+(disciplina!=null ? disciplina.getId() : 0)+"; "+diaSemana+"; "+horaInicio+"; "+horaFim;
    }
}
