package com.mycompany.sistemadegestaoeagendamentodesalas.dto1;
import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva {
    private int id;
    private Sala sala;
    private Docente docente;
    private Disciplina disciplina;
    private Turma turma;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private EstadoReserva estado;
    private TipoReserva tipoReserva;
    private DiaSemana diaSemana;

    
    public Reserva(int id, Sala sala, Docente docente, Disciplina disciplina, Turma turma, LocalDate data, LocalTime horaInicio, LocalTime horaFim, TipoReserva tipoReserva, DiaSemana diaSemana){
        this.id = id;
        this.sala = sala;
        this.docente = docente;
        this.disciplina = disciplina;
        this.turma = turma;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.estado = EstadoReserva.PENDENTE;
        this.tipoReserva = tipoReserva;
        this.diaSemana = diaSemana;
    }

    public int getId(){return this.id;}

    public Sala getSalaId(){
        return this.sala;
    }
    public void setSalaId(Sala salaId){
        this.sala = salaId;
    }

    public Docente getDocenteId(){
        return this.docente;
    }
    public void setDocenteId(Docente docenteId){
        this.docente = docenteId;
    }


    public Disciplina getDisciplina(){return this.disciplina;}
    public void setDisciplina(Disciplina disciplina){this.disciplina = disciplina;}

    public Turma getTurma(){return this.turma;}
    public void setTurma(Turma turma){this.turma = turma;}

    public LocalDate getData(){return this.data;}
    public void setData(LocalDate data){this.data = data;}

    public LocalTime getHoraInicio(){return this.horaInicio;}
    public void setHoraInicio(LocalTime horaInicio){this.horaInicio=horaInicio;}

    public LocalTime getHoraFim(){return this.horaFim;}
    public void setHoraFim(LocalTime horaFim){this.horaFim=horaFim;}

    public EstadoReserva getEstadoReserva(){return this.estado;}
    public void setEstadoReserva(EstadoReserva estado){this.estado=estado;}

    public TipoReserva getTipoReserva(){return this.tipoReserva;}
    public void setTipoReserva(TipoReserva tipoReserva){this.tipoReserva=tipoReserva;}

    public DiaSemana getDiaSemana(){return this.diaSemana;}
    public void setDiaSemana(DiaSemana diaSemana){this.diaSemana=diaSemana;}

    @Override
    public String toString(){
        return id+"; "+getSalaId()+"; "+getDocenteId()+"; "+(disciplina!=null?disciplina.getId():0)+"; "+(turma!=null?turma.getChave():"")+"; "+data+"; "+horaInicio+"; "+horaFim+"; "+(tipoReserva!=null?tipoReserva:"")+"; "+(diaSemana!=null?diaSemana:"")+"; "+estado;
    }
}
