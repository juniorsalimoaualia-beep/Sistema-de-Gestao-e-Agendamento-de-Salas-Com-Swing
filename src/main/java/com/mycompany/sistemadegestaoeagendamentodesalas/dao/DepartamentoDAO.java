package com.mycompany.sistemadegestaoeagendamentodesalas.dao;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Departamento;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Curso;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Sala;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Secretario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;


public class DepartamentoDAO {
    private final SecretarioDAO secretarioDAO = new SecretarioDAO();

    public void salvar(Departamento dp){
        if (dp == null || dp.getSecretario() == null || dp.getSecretario().getId() <= 0) {
            throw new IllegalArgumentException("Associe um secretario ao departamento.");
        }
        String sql = "INSERT INTO departamentos(id, nome, curso_id, sala_id, secretario_id) "
                + "VALUES(?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE nome = VALUES(nome), "
                + "curso_id = VALUES(curso_id), sala_id = VALUES(sala_id), secretario_id = VALUES(secretario_id)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, dp.getId());
            statement.setString(2, dp.getNome());
            if (dp.getCurso() == null || dp.getCurso().getId() == 0) statement.setNull(3, java.sql.Types.INTEGER);
            else statement.setInt(3, dp.getCurso().getId());
            if (dp.getSala() == null || dp.getSala().getId() == 0) statement.setNull(4, java.sql.Types.INTEGER);
            else statement.setInt(4, dp.getSala().getId());
            statement.setInt(5, dp.getSecretario().getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao salvar departamento.", e);
        }
    }

    public List<Departamento> listaDepartamento(){
        List<Departamento> lista=new ArrayList<>();
        String sql = "SELECT id, nome, secretario_id FROM departamentos ORDER BY id";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) lista.add(lerDepartamento(result));
        } catch (SQLException e) { System.out.println("Erro ao listar departamentos: " + e.getMessage()); }
        return lista;
    }

    public Departamento buscarPorId(int id){
        String sql = "SELECT id, nome, secretario_id FROM departamentos WHERE id = ?";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) return lerDepartamento(result);
            }
        } catch (SQLException e) { System.out.println("Erro ao procurar departamento: " + e.getMessage()); }
        return null;
    }

    public Departamento buscarPorEmailSecretario(String email) {
        String sql = "SELECT d.id, d.nome, d.secretario_id FROM departamentos d "
                + "JOIN usuarios u ON u.id = d.secretario_id "
                + "WHERE u.perfil = 'SECRETARIO' AND lower(u.email) = lower(?)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email == null ? "" : email.trim());
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? lerDepartamento(result) : null;
            }
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("secretario_id")) {
                throw new IllegalStateException("A base de dados ainda nao tem a coluna do secretario "
                        + "por departamento. Execute database/migrations/004_horario_sala_secretario_departamento.sql.", e);
            }
            throw new IllegalStateException("Erro ao procurar departamento do secretario.", e);
        }
    }

    public boolean atribuirSecretario(int departamentoId, int secretarioId) {
        String sql = "UPDATE departamentos SET secretario_id = ? WHERE id = ?";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, secretarioId);
            statement.setInt(2, departamentoId);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Nao foi possivel atribuir o secretario ao departamento.", e);
        }
    }

    private Departamento lerDepartamento(ResultSet result) throws SQLException {
        Departamento departamento = new Departamento(result.getInt("id"), result.getString("nome"));
        int secretarioId = result.getInt("secretario_id");
        if (!result.wasNull()) {
            Secretario secretario = secretarioDAO.buscarPorSecretario(secretarioId);
            if (secretario != null) {
                secretario.setDepartamento(departamento);
                departamento.setSecretario(secretario);
            }
        }
        return departamento;
    }

    public String buscarPorDepartamento(int id){
        Departamento departamento = buscarPorId(id);
        return departamento != null ? departamento.getNome() : "Departamento nao encontrado";
    }

    public int gerarProximoId(){
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement("SELECT COALESCE(MAX(id), 0) + 1 FROM departamentos");
             ResultSet result = statement.executeQuery()) {
            return result.next() ? result.getInt(1) : 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao gerar ID do departamento.", e);
        }
    }
}
