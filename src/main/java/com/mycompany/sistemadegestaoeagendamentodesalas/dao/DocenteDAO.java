package com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mycompany.sistemadegestaoeagendamentodesalas.model.Docente;
public class DocenteDAO {
    public int gerarProximoId() {
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COALESCE(MAX(id), 0) + 1 FROM usuarios");
             ResultSet result = statement.executeQuery()) {
            return result.next() ? result.getInt(1) : 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao gerar ID do docente: " + e.getMessage(), e);
        }
    }

    public void salvar(Docente dc){
        String sql = "INSERT INTO usuarios(id, perfil, nome, apelido, nivel_academico, celular, email, senha_hash) "
                + "VALUES(?, 'DOCENTE', ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE "
                + "perfil = VALUES(perfil), nome = VALUES(nome), apelido = VALUES(apelido), "
                + "nivel_academico = VALUES(nivel_academico), celular = VALUES(celular), "
                + "email = VALUES(email), senha_hash = VALUES(senha_hash)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, dc.getId());
            statement.setString(2, dc.getNome());
            statement.setString(3, dc.getApelido());
            statement.setString(4, dc.getNivelAcademico());
            statement.setInt(5, dc.getNumCel());
            statement.setString(6, dc.getEmail());
            statement.setString(7, AutenticacaoDAO.prepararSenha(dc.getSenha()));
            statement.executeUpdate();
        } catch (SQLException e) { System.out.println("Erro ao salvar docente: " + e.getMessage()); }
    }

    public List<Docente> listaDocente(){
        List<Docente> lista=new ArrayList<>();
        String sql = "SELECT id, nome, apelido, nivel_academico, celular, email, senha_hash "
                + "FROM usuarios WHERE perfil = 'DOCENTE' ORDER BY id";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) lista.add(new Docente(result.getInt("id"), result.getString("nome"),
                    result.getString("apelido"), result.getString("nivel_academico"), result.getInt("celular"),
                    result.getString("email"), result.getString("senha_hash")));
        } catch (SQLException e) { System.out.println("Erro ao listar docentes: " + e.getMessage()); }
        return lista;
    }

    public boolean autenticarDocente(String email, String senha){
        return autenticar(perfil(), email, senha);
    }

    private String perfil() { return "DOCENTE"; }

    private boolean autenticar(String perfil, String email, String senha) {
        String sql = "SELECT senha_hash FROM usuarios WHERE perfil = ? AND lower(email) = lower(?)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, perfil);
            statement.setString(2, email);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() && AutenticacaoDAO.validarHash(senha, result.getString(1));
            }
        } catch (SQLException e) { System.out.println("Erro ao autenticar docente: " + e.getMessage()); return false; }
    }

    public Docente buscarPorDocente(int id){
        String sql = "SELECT id, nome, apelido, nivel_academico, celular, email, senha_hash "
                + "FROM usuarios WHERE perfil = 'DOCENTE' AND id = ?";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) return new Docente(result.getInt("id"), result.getString("nome"),
                        result.getString("apelido"), result.getString("nivel_academico"), result.getInt("celular"),
                        result.getString("email"), result.getString("senha_hash"));
            }
        } catch (SQLException e) { System.out.println("Erro ao procurar docente: " + e.getMessage()); }
        return null;
    }

    public boolean alterarSenha(int id, String senhaAtual, String senhaNova) {
        Docente docente = buscarPorDocente(id);
        if (docente == null || !AutenticacaoDAO.validarHash(senhaAtual, docente.getSenha())) return false;
        String sql = "UPDATE usuarios SET senha_hash = ? WHERE id = ? AND perfil = 'DOCENTE'";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, AutenticacaoDAO.criarHash(senhaNova));
            statement.setInt(2, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) { System.out.println("Erro ao alterar senha: " + e.getMessage()); return false; }
    }

    public void atualizarTodos(List<Docente> lista) {
        String sql = "INSERT INTO usuarios(id, perfil, nome, apelido, nivel_academico, celular, email, senha_hash) "
                + "VALUES(?, 'DOCENTE', ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE "
                + "perfil = VALUES(perfil), nome = VALUES(nome), apelido = VALUES(apelido), "
                + "nivel_academico = VALUES(nivel_academico), celular = VALUES(celular), "
                + "email = VALUES(email), senha_hash = VALUES(senha_hash)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement delete = connection.prepareStatement("DELETE FROM usuarios WHERE perfil = 'DOCENTE'");
             PreparedStatement insert = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            try {
                delete.executeUpdate();
                for (Docente docente : lista) {
                    insert.setInt(1, docente.getId());
                    insert.setString(2, docente.getNome());
                    insert.setString(3, docente.getApelido());
                    insert.setString(4, docente.getNivelAcademico());
                    insert.setInt(5, docente.getNumCel());
                    insert.setString(6, docente.getEmail());
                    insert.setString(7, AutenticacaoDAO.prepararSenha(docente.getSenha()));
                    insert.addBatch();
                }
                insert.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar docentes: " + e.getMessage());
        }
    }
}
