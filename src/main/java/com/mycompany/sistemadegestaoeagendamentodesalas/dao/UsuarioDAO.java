package com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import java.util.List;
import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Usuario;

public interface UsuarioDAO<T extends Usuario> {
    void salvar(T usuario);
    List<T> listar();
    T buscarPorId(int id);
    int gerarProximoId();
    boolean alterarSenha(int id, String senhaAtual, String senhaNova);
    void reescreverArquivo(List<T> lista);
}
