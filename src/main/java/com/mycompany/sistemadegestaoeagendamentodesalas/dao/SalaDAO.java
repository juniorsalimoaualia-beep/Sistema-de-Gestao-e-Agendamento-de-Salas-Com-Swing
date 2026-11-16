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
        String sql = "INSERT INTO salas(id, nome, estado, reserva_id, departamento_id) VALUES(?, ?, ?, ?, ?)";
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
        atualizarEstadoReservasExpiradas();
        List<Sala> lista=new ArrayList<>();
        String sql = "SELECT id, nome, estado, reserva_id, departamento_id FROM salas ORDER BY id";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) lista.add(lerSala(result));
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar salas.", e);
        }
        return lista;
    }

    public Sala buscarPorSala(String nome){
        atualizarEstadoReservasExpiradas();
        String sql = "SELECT id, nome, estado, reserva_id, departamento_id FROM salas WHERE lower(nome) = lower(?) LIMIT 1";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nome);
            try (ResultSet result = statement.executeQuery()) { if (result.next()) return lerSala(result); }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao procurar sala.", e);
        }
        return null;
    }

    private void atualizarEstadoReservasExpiradas() {
        String sql = "UPDATE salas s SET s.reserva_id = COALESCE(("
                + "SELECT r.id FROM reservas r WHERE r.sala_id = s.id "
                + "AND r.estado IN ('PENDENTE', 'CONFIRMADA') "
                + "AND (r.data > CURRENT_DATE OR (r.data = CURRENT_DATE AND r.hora_fim > CURRENT_TIME)) "
                + "ORDER BY r.data, r.hora_inicio, r.id LIMIT 1), -1), "
                + "s.estado = CASE WHEN s.estado = 'MANUTENCAO' THEN s.estado "
                + "WHEN EXISTS (SELECT 1 FROM reservas r WHERE r.sala_id = s.id "
                + "AND r.estado IN ('PENDENTE', 'CONFIRMADA') "
                + "AND (r.data > CURRENT_DATE OR (r.data = CURRENT_DATE AND r.hora_fim > CURRENT_TIME))) "
                + "THEN 'RESERVADA' WHEN s.estado = 'RESERVADA' THEN 'LIVRE' ELSE s.estado END";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Nao foi possivel atualizar o estado das salas.", e);
        }
    }

    public Sala buscarPorId(int id) {
        String sql = "SELECT id, nome, estado, reserva_id, departamento_id FROM salas WHERE id = ?";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) { if (result.next()) return lerSala(result); }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao procurar sala.", e);
        }
        return null;
    }

    public boolean alterarDepartamento(int salaId, int departamentoId) {
        String sql = "UPDATE salas SET departamento_id = ? WHERE id = ?";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, departamentoId);
            statement.setInt(2, salaId);
            if (statement.executeUpdate() > 0) return true;
            try (PreparedStatement existe = connection.prepareStatement(
                    "SELECT 1 FROM salas WHERE id = ?")) {
                existe.setInt(1, salaId);
                try (ResultSet resultado = existe.executeQuery()) {
                    return resultado.next();
                }
            }
        } catch (SQLException e) {
            if ("23000".equals(e.getSQLState())) {
                throw new IllegalStateException("Ja existe uma sala com esse nome no departamento selecionado.", e);
            }
            throw new IllegalStateException("Nao foi possivel alterar o departamento da sala.", e);
        }
    }

    public boolean removerPorId(int salaId) {
        String referencias = "SELECT (SELECT COUNT(*) FROM horarios WHERE sala_id = ?) "
                + "+ (SELECT COUNT(*) FROM reservas WHERE sala_id = ?)";
        try (Connection connection = ConexaoSql.conectar()) {
            connection.setAutoCommit(false);
            try (PreparedStatement verificar = connection.prepareStatement(referencias)) {
                verificar.setInt(1, salaId);
                verificar.setInt(2, salaId);
                try (ResultSet resultado = verificar.executeQuery()) {
                    if (resultado.next() && resultado.getInt(1) > 0) {
                        connection.rollback();
                        throw new IllegalStateException("Nao e possivel eliminar a sala porque esta "
                                + "associada a horarios ou reservas.");
                    }
                }
            }
            try (PreparedStatement remover = connection.prepareStatement(
                    "DELETE FROM salas WHERE id = ?")) {
                remover.setInt(1, salaId);
                boolean removida = remover.executeUpdate() > 0;
                connection.commit();
                return removida;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao eliminar sala.", e);
        }
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
        String sql = "UPDATE salas SET nome = ?, estado = ?, reserva_id = ?, departamento_id = ? WHERE id = ?";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            try {
                for (Sala sala : lista) {
                    statement.setString(1, sala.getNome());
                    statement.setString(2, sala.getEstado().name());
                    statement.setInt(3, sala.getReservaId());
                    if (sala.getDepartamento() == null) statement.setNull(4, java.sql.Types.INTEGER);
                    else statement.setInt(4, sala.getDepartamento().getId());
                    statement.setInt(5, sala.getId());
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
