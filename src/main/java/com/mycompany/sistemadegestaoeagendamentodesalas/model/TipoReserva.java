package com.mycompany.sistemadegestaoeagendamentodesalas.model;

public enum TipoReserva {
    AULA, EVENTO, EXAME, TESTE;

    /** A aula ao sabado e uma excecao manual; os restantes tipos aceitam segunda a sabado. */
    public boolean permiteDia(DiaSemana dia) {
        if (dia == null) {
            return false;
        }
        if (dia != DiaSemana.SABADO) {
            return true;
        }
        switch (this) {
            case AULA:
                return true;
            case EVENTO:
            case EXAME:
            case TESTE:
                return true;
            default:
                return false;
        }
    }
}
