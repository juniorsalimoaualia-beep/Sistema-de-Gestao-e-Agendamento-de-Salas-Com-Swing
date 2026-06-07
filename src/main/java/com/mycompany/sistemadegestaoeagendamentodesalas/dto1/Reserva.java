package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1;
import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva {
    private int id;
    private int salaId;
    private int docenteId;
    private Disciplina disciplina;
    private Turma turma;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private EstadoReserva estado;

    public Reserva(int id, int salaId, int docenteId, Disciplina disciplina, Turma turma, LocalDate data, LocalTime horaInicio, LocalTime horaFim){
        this.id = id;
        this.salaId = salaId;
        this.docenteId = docenteId;
        this.disciplina = disciplina;
        this.turma = turma;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.estado = EstadoReserva.PENDENTE;
    }

    public int getId(){return this.id;}

    public int getSalaId(){return this.salaId;}
    public void setSalaId(int salaId){this.salaId = salaId;}

    public int getDocenteId(){return this.docenteId;}
    public void setDocenteId(int docenteId){this.docenteId = docenteId;}

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

    @Override
    public String toString(){
        return id+"; "+salaId+"; "+docenteId+"; "+(disciplina!=null?disciplina.getId():0)+"; "+(turma!=null?turma.getChave():"")+"; "+data+"; "+horaInicio+"; "+horaFim+"; "+estado;
    }
}