package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1;
import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva {
    private int id;
    private int salaId;
    private String docenteNome;
    private int docenteId;
    private String disciplina;
    private String turma;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private EstadoReserva estado;

    public Reserva(int id, int salaId, int docenteId, String docenteNome, String disciplina, String turma, LocalDate data, LocalTime horaInicio, LocalTime horaFim){
        this.id = id;
        this.salaId = salaId;
        this.docenteId = docenteId;
        this.docenteNome = docenteNome != null ? docenteNome : "";
        this.disciplina = disciplina != null ? disciplina : "";
        this.turma = turma != null ? turma : "";
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

    public String getDocenteNome(){return this.docenteNome;}
    public void setDocenteNome(String docenteNome){this.docenteNome = docenteNome;}

    public String getDisciplina(){return this.disciplina;}

    public String getTurma(){return this.turma;}

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
        return id+"; "+salaId+"; "+docenteId+"; "+docenteNome+"; "+disciplina+"; "+turma+"; "+data+"; "+horaInicio+"; "+horaFim+"; "+estado;
    }
}