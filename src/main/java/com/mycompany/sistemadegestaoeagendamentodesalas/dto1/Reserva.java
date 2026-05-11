package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1;
import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva {
    private int id;
    private String sala;
    private String docente;
    private String disciplina;
    private String turma;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private EstadoReserva estado;

    public Reserva(int id, Sala sala, Docente docente, String disciplina, String turma, LocalDate data, LocalTime horaInicio, LocalTime horaFim){
        this.id = id;
        this.sala = sala != null ? sala.getNome() : null;
        this.docente = docente != null ? docente.getNomeCompleto() : null;
        this.disciplina = disciplina != null ? disciplina : "";
        this.turma = turma != null ? turma : "";
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.estado = EstadoReserva.PENDENTE;
    }

    public int getId(){return this.id;}

    public String getSala(){return this.sala;}

    public String getDocente(){return this.docente;}

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
    public String toString(){
        return id+"; "+sala+"; "+docente+"; "+disciplina+"; "+/*turma.get*/data+"; "+horaInicio+"; "+horaFim;
    }
}