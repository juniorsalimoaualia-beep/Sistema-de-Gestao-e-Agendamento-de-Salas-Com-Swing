package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1;

public class Sala{
    private int id;
    private String nome;
    private EstadoSala estado;
    private int reservaId;

    public Sala(int id, String nome){
        this.id=id;
        this.nome=nome;
        this.estado = EstadoSala.LIVRE;
        this.reservaId = -1;
    }

    public int getId(){return this.id;}

    public String getNome(){return this.nome;}
    public void setNome(String nome){this.nome=nome;}

    public EstadoSala getEstado(){return this.estado;}
    public void setEstado(EstadoSala estado){this.estado=estado;}

    public int getReservaId(){return this.reservaId;}
    public void setReservaId(int reservaId){this.reservaId=reservaId;}

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
        return id+"; "+nome+"; "+estado+"; "+reservaId;
    }
}
