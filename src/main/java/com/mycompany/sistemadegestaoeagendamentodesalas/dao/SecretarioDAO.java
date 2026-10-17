package com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mycompany.sistemadegestaoeagendamentodesalas.model.Secretario;

public class SecretarioDAO {
    public int gerarProximoId() {
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COALESCE(MAX(id), 0) + 1 FROM usuarios");
             ResultSet result = statement.executeQuery()) {
            return result.next() ? result.getInt(1) : 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao gerar ID do secretario: " + e.getMessage(), e);
        }
    }

    public void salvar(Secretario dp){
        String sql = "INSERT INTO usuarios(id, perfil, nome, apelido, cargo, celular, email, senha_hash) "
                + "VALUES(?, 'SECRETARIO', ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE "
                + "perfil = VALUES(perfil), nome = VALUES(nome), apelido = VALUES(apelido), "
                + "cargo = VALUES(cargo), celular = VALUES(celular), email = VALUES(email), "
                + "senha_hash = VALUES(senha_hash)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, dp.getId()); statement.setString(2, dp.getNome());
            statement.setString(3, dp.getApelido()); statement.setString(4, dp.getCargo());
            statement.setInt(5, dp.getNumCel()); statement.setString(6, dp.getEmail());
            statement.setString(7, AutenticacaoDAO.prepararSenha(dp.getSenha())); statement.executeUpdate();
        } catch (SQLException e) { System.out.println("Erro ao salvar secretario: " + e.getMessage()); }
    }

    public List<Secretario> listaSecretario(){
        List<Secretario> lista=new ArrayList<>();
        String sql = "SELECT id, nome, apelido, cargo, celular, email, senha_hash "
                + "FROM usuarios WHERE perfil = 'SECRETARIO' ORDER BY id";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) lista.add(lerSecretario(result));
        } catch (SQLException e) { System.out.println("Erro ao listar secretarios: " + e.getMessage()); }
        return lista;
    }

    private Secretario lerSecretario(ResultSet result) throws SQLException {
        return new Secretario(result.getInt("id"), result.getString("nome"), result.getString("apelido"),
                result.getString("cargo"), result.getInt("celular"), result.getString("email"), result.getString("senha_hash"));
    }

    public Secretario buscarPorSecretario(int id){
        String sql = "SELECT id, nome, apelido, cargo, celular, email, senha_hash FROM usuarios WHERE perfil = 'SECRETARIO' AND id = ?";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) { if (result.next()) return lerSecretario(result); }
        } catch (SQLException e) { System.out.println("Erro ao procurar secretario: " + e.getMessage()); }
        return null;
    }

    public boolean alterarSenha(int id, String senhaAtual, String senhaNova) {
        Secretario secretario = buscarPorSecretario(id);
        if (secretario == null || !AutenticacaoDAO.validarHash(senhaAtual, secretario.getSenha())) return false;
        String sql = "UPDATE usuarios SET senha_hash = ? WHERE id = ? AND perfil = 'SECRETARIO'";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, AutenticacaoDAO.criarHash(senhaNova)); statement.setInt(2, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) { System.out.println("Erro ao alterar senha: " + e.getMessage()); return false; }
    }

    public void atualizarTodos(List<Secretario> lista) {
        String sql = "INSERT INTO usuarios(id, perfil, nome, apelido, cargo, celular, email, senha_hash) "
                + "VALUES(?, 'SECRETARIO', ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE "
                + "perfil = VALUES(perfil), nome = VALUES(nome), apelido = VALUES(apelido), "
                + "cargo = VALUES(cargo), celular = VALUES(celular), email = VALUES(email), "
                + "senha_hash = VALUES(senha_hash)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement delete = connection.prepareStatement("DELETE FROM usuarios WHERE perfil = 'SECRETARIO'");
             PreparedStatement insert = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            try {
                delete.executeUpdate();
                for (Secretario secretario : lista) {
                    insert.setInt(1, secretario.getId());
                    insert.setString(2, secretario.getNome());
                    insert.setString(3, secretario.getApelido());
                    insert.setString(4, secretario.getCargo());
                    insert.setInt(5, secretario.getNumCel());
                    insert.setString(6, secretario.getEmail());
                    insert.setString(7, AutenticacaoDAO.prepararSenha(secretario.getSenha()));
                    insert.addBatch();
                }
                insert.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar secretarios: " + e.getMessage());
        }
    }
}
