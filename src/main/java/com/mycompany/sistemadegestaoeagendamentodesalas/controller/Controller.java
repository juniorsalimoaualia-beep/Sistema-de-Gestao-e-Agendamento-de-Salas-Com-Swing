package com.mycompany.sistemadegestaoeagendamentodesalas.controller;

import java.util.List;

public interface Controller<T> {
    List<T> listar();
    int gerarProximoId();
}