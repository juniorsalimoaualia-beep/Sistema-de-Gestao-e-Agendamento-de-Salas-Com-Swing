package com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.sistemadegestaoeagendamentodesalas.model.Sala;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.EstadoSala;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Departamento;

public class SalaDAO {
    private final DepartamentoDAO departamentoDAO = new DepartamentoDAO();

    public void salvar(Sala sala){
        String sql = "INSERT INTO salas(id, nome, estado, reserva_id, departamento_id) VALUES(?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE nome = VALUES(nome), estado = VALUES(estado), "
                + "reserva_id = VALUES(reserva_id), departamento_id = VALUES(departamento_id)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, sala.getId()); statement.setString(2, sala.getNome());
            statement.setString(3, sala.getEstado().name()); statement.setInt(4, sala.getReservaId());
            if (sala.getDepartamento() == null) statement.setNull(5, java.sql.Types.INTEGER);
            else statement.setInt(5, sala.getDepartamento().getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao salvar sala.", e);
        }
    }

    public List<Sala> listaSala(){
        List<Sala> lista=new ArrayList<>();
        String sql = "SELECT id, nome, estado, reserva_id, departamento_id FROM salas ORDER BY id";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) lista.add(lerSala(result));
        } catch (SQLException e) { System.out.println("Erro ao listar salas: " + e.getMessage()); }
        return lista;
    }

    public Sala buscarPorSala(String nome){
        String sql = "SELECT id, nome, estado, reserva_id, departamento_id FROM salas WHERE lower(nome) = lower(?) LIMIT 1";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nome);
            try (ResultSet result = statement.executeQuery()) { if (result.next()) return lerSala(result); }
        } catch (SQLException e) { System.out.println("Erro ao procurar sala: " + e.getMessage()); }
        return null;
    }

    public Sala buscarPorId(int id) {
        String sql = "SELECT id, nome, estado, reserva_id, departamento_id FROM salas WHERE id = ?";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) { if (result.next()) return lerSala(result); }
        } catch (SQLException e) { System.out.println("Erro ao procurar sala: " + e.getMessage()); }
        return null;
    }

    private Sala lerSala(ResultSet result) throws SQLException {
        Sala sala = new Sala(result.getInt("id"), result.getString("nome"));
        sala.setEstado(EstadoSala.valueOf(result.getString("estado")));
        sala.setReservaId(result.getInt("reserva_id"));
        int departamentoId = result.getInt("departamento_id");
        if (!result.wasNull()) {
            Departamento departamento = departamentoDAO.buscarPorId(departamentoId);
            sala.setDepartamento(departamento == null
                    ? new Departamento(departamentoId, "") : departamento);
        }
        return sala;
    }

    public void atualizar(Sala sala) {
        String sql = "UPDATE salas SET nome = ?, estado = ?, reserva_id = ?, departamento_id = ? WHERE id = ?";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, sala.getNome()); statement.setString(2, sala.getEstado().name());
            statement.setInt(3, sala.getReservaId());
            if (sala.getDepartamento() == null) statement.setNull(4, java.sql.Types.INTEGER);
            else statement.setInt(4, sala.getDepartamento().getId());
            statement.setInt(5, sala.getId()); statement.executeUpdate();
        } catch (SQLException e) { System.out.println("Erro ao atualizar sala: " + e.getMessage()); }
    }

    public int gerarProximoId(){
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement("SELECT COALESCE(MAX(id), 0) + 1 FROM salas");
             ResultSet result = statement.executeQuery()) { return result.next() ? result.getInt(1) : 1; }
        catch (SQLException e) {
            throw new IllegalStateException("Erro ao gerar ID da sala.", e);
        }
    }

    public void atualizarTodos(List<Sala> lista) {
        String sql = "INSERT INTO salas(id, nome, estado, reserva_id, departamento_id) VALUES(?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE nome = VALUES(nome), estado = VALUES(estado), "
                + "reserva_id = VALUES(reserva_id), departamento_id = VALUES(departamento_id)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            try {
                for (Sala sala : lista) {
                    statement.setInt(1, sala.getId()); statement.setString(2, sala.getNome());
                    statement.setString(3, sala.getEstado().name()); statement.setInt(4, sala.getReservaId());
                    if (sala.getDepartamento() == null) statement.setNull(5, java.sql.Types.INTEGER);
                    else statement.setInt(5, sala.getDepartamento().getId());
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) { System.out.println("Erro ao guardar salas: " + e.getMessage()); }
    }
}
