package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1;

public class Sala{
    private int id;
    private String nome;
    private EstadoSala estado;
    private int reservaId;
    private Departamento departamento;

    public Sala(int id, String nome){
        this(id, nome, null);
    }

    public Sala(int id, String nome, Departamento departamento){
        this.id=id;
        this.nome=nome;
        this.estado = EstadoSala.LIVRE;
        this.reservaId = -1;
        this.departamento = departamento;
    }

    public int getId(){return this.id;}

    public String getNome(){return this.nome;}
    public void setNome(String nome){this.nome=nome;}

    public EstadoSala getEstado(){return this.estado;}
    public void setEstado(EstadoSala estado){this.estado=estado;}

    public int getReservaId(){return this.reservaId;}
    public void setReservaId(int reservaId){this.reservaId=reservaId;}

    public Departamento getDepartamento(){
        return this.departamento;
    }

    public void setDepartamento(Departamento departamento){
        this.departamento = departamento;
    }

    public void vincularReserva(int reservaId) {
        this.reservaId = reservaId;
        this.estado = EstadoSala.RESERVADA;
    }

    public void desvinculaReserva() {
        this.reservaId = -1;
        this.estado = EstadoSala.LIVRE;
    }

    @Override
    public String toString(){
        int departamentoId = departamento != null ? departamento.getId() : -1;
        return id+"; "+nome+"; "+estado+"; "+reservaId+"; "+departamentoId;
    }
}
