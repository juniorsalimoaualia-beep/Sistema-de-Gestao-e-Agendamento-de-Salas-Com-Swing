package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.controller;

import java.util.List;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Usuario;

public interface UsuarioController<T extends Usuario> {
    void salvar(T usuario);
    List<T> listar();
    T buscarPorId(int id);
    int gerarProximoId();
    boolean alterarSenha(int id, String senhaAtual, String senhaNova);
}
