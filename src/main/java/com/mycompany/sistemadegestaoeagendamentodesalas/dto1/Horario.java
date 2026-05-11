package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1;
import java.time.LocalTime;
public class Horario {
    private int id;
    private String nomeCurso;
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    public Horario(int id, String  nomeCurso, DiaSemana dia, LocalTime horaInicio, LocalTime horaFim){
        this.id=id;
        this.nomeCurso=nomeCurso;
        this.diaSemana=dia;
        this.horaInicio=horaInicio;
        this.horaFim=horaFim;
    }

    public int getId(){return this.id;}

    public String getNomeCurso(){return this.nomeCurso;}
    public void setNome(String nome){this.nomeCurso=nome;}

    public DiaSemana getDiaSemana(){return diaSemana;}
    public void setDiaSemana(DiaSemana dia){this.diaSemana=dia;}

    public LocalTime getHoraInicio(){return this.horaInicio;}
    public void setHoraInicio(LocalTime horaIni){this.horaInicio=horaIni;}

    public LocalTime getHoraFim(){return horaFim;}
    public void setHoraFim(LocalTime horaFim){this.horaFim=horaFim;}

    public String toString(){
        return id+"; "+nomeCurso+"; "+diaSemana+"; "+horaInicio+"; "+horaFim;
    }
}
