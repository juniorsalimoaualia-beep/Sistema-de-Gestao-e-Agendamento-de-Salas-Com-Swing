package com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import com.mycompany.sistemadegestaoeagendamentodesalas.model.DiaSemana;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Disciplina;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Docente;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.EstadoReserva;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.EstadoSala;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Reserva;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Sala;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.TipoReserva;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Turma;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {
    private final SalaDAO salaDAO = new SalaDAO();
    private final DocenteDAO docenteDAO = new DocenteDAO();
    private final DisciplinaDAO disciplinaDAO = new DisciplinaDAO();
    private final TurmaDAO turmaDAO = new TurmaDAO();

    public boolean salvar(Reserva reserva) {
        if (reserva == null || reserva.getSalaId() == null || reserva.getDocenteId() == null
                || reserva.getData() == null || reserva.getHoraInicio() == null || reserva.getHoraFim() == null
                || reserva.getTipoReserva() == null || reserva.getDiaSemana() == null
                || (reserva.getTipoReserva() == TipoReserva.AULA && reserva.getDisciplina() == null)
                || reserva.getEstadoReserva() != EstadoReserva.PENDENTE
                || reserva.getSalaId().getId() <= 0 || reserva.getDocenteId().getId() <= 0
                || (reserva.getTurma() != null && reserva.getTurma().getId() <= 0)
                || reserva.getData().isBefore(LocalDate.now())
                || !reserva.getHoraFim().isAfter(reserva.getHoraInicio())
                || diaDaSemana(reserva.getData()) != reserva.getDiaSemana()) {
            return false;
        }
        String sql = "INSERT INTO reservas(id, sala_id, docente_id, disciplina_id, turma_id, data, hora_inicio, "
                + "hora_fim, tipo, dia_semana, estado) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConexaoSql.conectar()) {
            connection.setAutoCommit(false);
            try {
                if (!salaDisponivel(connection, reserva.getSalaId().getId())) {
                    connection.rollback();
                    return false;
                }
                if (temConflito(connection, reserva.getSalaId().getId(), reserva.getDocenteId().getId(),
                        turmaId(reserva), reserva.getData(), reserva.getHoraInicio(), reserva.getHoraFim(),
                        reserva.getId())) {
                    connection.rollback();
                    return false;
                }

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, reserva.getId());
                    statement.setInt(2, reserva.getSalaId().getId());
                    statement.setInt(3, reserva.getDocenteId().getId());
                    if (reserva.getDisciplina() == null) statement.setNull(4, java.sql.Types.INTEGER);
                    else statement.setInt(4, reserva.getDisciplina().getId());
                    if (reserva.getTurma() == null) statement.setNull(5, java.sql.Types.INTEGER);
                    else statement.setInt(5, reserva.getTurma().getId());
                    statement.setString(6, reserva.getData().toString());
                    statement.setString(7, reserva.getHoraInicio().toString());
                    statement.setString(8, reserva.getHoraFim().toString());
                    statement.setString(9, reserva.getTipoReserva().name());
                    statement.setString(10, reserva.getDiaSemana().name());
                    statement.setString(11, EstadoReserva.PENDENTE.name());
                    if (statement.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement(
                        "UPDATE salas SET reserva_id = ?, estado = 'RESERVADA' WHERE id = ?")) {
                    statement.setInt(1, reserva.getId());
                    statement.setInt(2, reserva.getSalaId().getId());
                    if (statement.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }
                }
                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao salvar reserva: " + e.getMessage());
            return false;
        }
    }

    private int turmaId(Reserva reserva) {
        return reserva.getTurma() == null ? 0 : reserva.getTurma().getId();
    }

    private boolean salaDisponivel(Connection connection, int salaId) throws SQLException {
        String sql = "SELECT estado FROM salas WHERE id = ? FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, salaId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() && !EstadoSala.MANUTENCAO.name().equals(result.getString("estado"));
            }
        }
    }

    boolean temConflito(int salaId, Integer docenteId, Integer turmaId, LocalDate data,
            LocalTime inicio, LocalTime fim, int reservaIgnorada) {
        try (Connection connection = ConexaoSql.conectar()) {
            return temConflito(connection, salaId, docenteId, turmaId, data, inicio, fim, reservaIgnorada);
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao verificar conflitos da reserva.", e);
        }
    }

    private boolean temConflito(Connection connection, int salaId, Integer docenteId, Integer turmaId,
            LocalDate data, LocalTime inicio, LocalTime fim, int reservaIgnorada) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT 1 FROM reservas WHERE data = ? AND estado IN ('PENDENTE', 'CONFIRMADA') AND (sala_id = ?");
        if (docenteId != null) {
            sql.append(" OR docente_id = ?");
        }
        if (turmaId != null && turmaId > 0) {
            sql.append(" OR turma_id = ?");
        }
        sql.append(") AND id <> ? AND hora_inicio < ? AND hora_fim > ? LIMIT 1");

        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            int parametro = 1;
            statement.setString(parametro++, data.toString());
            statement.setInt(parametro++, salaId);
            if (docenteId != null) {
                statement.setInt(parametro++, docenteId);
            }
            if (turmaId != null && turmaId > 0) {
                statement.setInt(parametro++, turmaId);
            }
            statement.setInt(parametro++, reservaIgnorada);
            statement.setString(parametro++, fim.toString());
            statement.setString(parametro, inicio.toString());
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    private DiaSemana diaDaSemana(LocalDate data) {
        switch (data.getDayOfWeek()) {
            case MONDAY: return DiaSemana.SEGUNDA;
            case TUESDAY: return DiaSemana.TERCA;
            case WEDNESDAY: return DiaSemana.QUARTA;
            case THURSDAY: return DiaSemana.QUINTA;
            case FRIDAY: return DiaSemana.SEXTA;
            case SATURDAY: return DiaSemana.SABADO;
            default: return null;
        }
    }

    public List<Reserva> listarReservas() {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT * FROM reservas ORDER BY data, hora_inicio";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) lista.add(lerReserva(result));
        } catch (SQLException e) {
            System.out.println("Erro ao listar reservas: " + e.getMessage());
        }
        return lista;
    }

    private Reserva lerReserva(ResultSet result) throws SQLException {
        Sala sala = salaDAO.buscarPorId(result.getInt("sala_id"));
        Docente docente = docenteDAO.buscarPorDocente(result.getInt("docente_id"));
        Disciplina disciplina = disciplinaDAO.buscarPorId(result.getInt("disciplina_id"));
        int turmaId = result.getInt("turma_id");
        Turma turma = null;
        if (!result.wasNull()) {
            for (Turma item : turmaDAO.listaTurma()) {
                if (item.getId() == turmaId) turma = item;
            }
        }
        Reserva reserva = new Reserva(result.getInt("id"), sala, docente, disciplina, turma,
                LocalDate.parse(result.getString("data")), LocalTime.parse(result.getString("hora_inicio")),
                LocalTime.parse(result.getString("hora_fim")), TipoReserva.valueOf(result.getString("tipo")),
                DiaSemana.valueOf(result.getString("dia_semana")));
        reserva.setEstadoReserva(EstadoReserva.valueOf(result.getString("estado")));
        return reserva;
    }

    public boolean isReservaDuplicada(Reserva reserva) {
        if (reserva == null || reserva.getSalaId() == null || reserva.getData() == null
                || reserva.getHoraInicio() == null || reserva.getHoraFim() == null) {
            return false;
        }
        Integer docenteId = reserva.getDocenteId() == null ? null : reserva.getDocenteId().getId();
        Integer turmaId = reserva.getTurma() == null ? null : reserva.getTurma().getId();
        return temConflito(reserva.getSalaId().getId(), docenteId, turmaId, reserva.getData(),
                reserva.getHoraInicio(), reserva.getHoraFim(), reserva.getId());
    }

    public Reserva buscarPorReserva(int id) {
        String sql = "SELECT * FROM reservas WHERE id = ?";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) { if (result.next()) return lerReserva(result); }
        } catch (SQLException e) {
            System.out.println("Erro ao procurar reserva: " + e.getMessage());
        }
        return null;
    }

    public int gerarProximoId() {
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement("SELECT COALESCE(MAX(id), 0) + 1 FROM reservas");
             ResultSet result = statement.executeQuery()) {
            return result.next() ? result.getInt(1) : 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao gerar ID da reserva.", e);
        }
    }

    public boolean excluirReserva(int id) {
        String buscarSala = "SELECT sala_id FROM reservas WHERE id = ?";
        String removerReserva = "DELETE FROM reservas WHERE id = ?";
        try (Connection connection = ConexaoSql.conectar()) {
            connection.setAutoCommit(false);
            try {
                int salaId = 0;
                try (PreparedStatement statement = connection.prepareStatement(buscarSala)) {
                    statement.setInt(1, id);
                    try (ResultSet result = statement.executeQuery()) {
                        if (result.next()) {
                            salaId = result.getInt("sala_id");
                        }
                    }
                }
                bloquearSala(connection, salaId);

                try (PreparedStatement statement = connection.prepareStatement(removerReserva)) {
                    statement.setInt(1, id);
                    if (statement.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }
                }

                if (salaId != 0) {
                    atualizarEstadoDaSala(connection, salaId);
                }
                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao excluir reserva: " + e.getMessage());
            return false;
        }
    }

    public boolean cancelarReserva(int id) {
        String encontrarReserva = "SELECT sala_id FROM reservas WHERE id = ? "
                + "AND estado IN ('PENDENTE', 'CONFIRMADA')";
        String bloquearReserva = "SELECT sala_id FROM reservas WHERE id = ? "
                + "AND estado IN ('PENDENTE', 'CONFIRMADA') FOR UPDATE";
        String cancelarReserva = "UPDATE reservas SET estado = 'CANCELADA' WHERE id = ? "
                + "AND estado IN ('PENDENTE', 'CONFIRMADA')";
        try (Connection connection = ConexaoSql.conectar()) {
            connection.setAutoCommit(false);
            try {
                int salaId;
                try (PreparedStatement statement = connection.prepareStatement(encontrarReserva)) {
                    statement.setInt(1, id);
                    try (ResultSet result = statement.executeQuery()) {
                        if (!result.next()) {
                            connection.rollback();
                            return false;
                        }
                        salaId = result.getInt("sala_id");
                    }
                }

                bloquearSala(connection, salaId);
                try (PreparedStatement statement = connection.prepareStatement(bloquearReserva)) {
                    statement.setInt(1, id);
                    try (ResultSet result = statement.executeQuery()) {
                        if (!result.next()) {
                            connection.rollback();
                            return false;
                        }
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement(cancelarReserva)) {
                    statement.setInt(1, id);
                    if (statement.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }
                }

                atualizarEstadoDaSala(connection, salaId);
                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao cancelar reserva: " + e.getMessage());
            return false;
        }
    }

    public boolean recusarReserva(int id) {
        try (Connection connection = ConexaoSql.conectar()) {
            connection.setAutoCommit(false);
            try {
                int salaId;
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT sala_id FROM reservas WHERE id = ? AND estado = 'PENDENTE' FOR UPDATE")) {
                    statement.setInt(1, id);
                    try (ResultSet result = statement.executeQuery()) {
                        if (!result.next()) {
                            connection.rollback();
                            return false;
                        }
                        salaId = result.getInt("sala_id");
                    }
                }
                bloquearSala(connection, salaId);
                try (PreparedStatement statement = connection.prepareStatement(
                        "UPDATE reservas SET estado = 'RECUSADA' WHERE id = ? AND estado = 'PENDENTE'")) {
                    statement.setInt(1, id);
                    if (statement.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }
                }
                atualizarEstadoDaSala(connection, salaId);
                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao recusar reserva: " + e.getMessage());
            return false;
        }
    }

    private void bloquearSala(Connection connection, int salaId) throws SQLException {
        if (salaId <= 0) {
            return;
        }
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id FROM salas WHERE id = ? FOR UPDATE")) {
            statement.setInt(1, salaId);
            try (ResultSet result = statement.executeQuery()) {
                result.next();
            }
        }
    }

    private void atualizarEstadoDaSala(Connection connection, int salaId) throws SQLException {
        String buscarReservaAtiva = "SELECT id FROM reservas WHERE sala_id = ? "
                + "AND estado IN ('PENDENTE', 'CONFIRMADA') ORDER BY data, hora_inicio, id LIMIT 1";
        int reservaAtivaId = -1;
        try (PreparedStatement statement = connection.prepareStatement(buscarReservaAtiva)) {
            statement.setInt(1, salaId);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    reservaAtivaId = result.getInt("id");
                }
            }
        }

        String sql;
        if (reservaAtivaId > 0) {
            sql = "UPDATE salas SET reserva_id = ?, "
                    + "estado = CASE WHEN estado = 'MANUTENCAO' THEN estado ELSE 'RESERVADA' END WHERE id = ?";
        } else {
            sql = "UPDATE salas SET reserva_id = -1, "
                    + "estado = CASE WHEN estado = 'MANUTENCAO' THEN estado ELSE 'LIVRE' END WHERE id = ?";
        }
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (reservaAtivaId > 0) {
                statement.setInt(1, reservaAtivaId);
                statement.setInt(2, salaId);
            } else {
                statement.setInt(1, salaId);
            }
            statement.executeUpdate();
        }
    }

    public void vincularSalaAReserva(int salaId, int reservaId) {
        String sql = "UPDATE salas SET reserva_id = ?, estado = 'RESERVADA' WHERE id = ?";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reservaId); statement.setInt(2, salaId); statement.executeUpdate();
        } catch (SQLException e) { System.out.println("Erro ao vincular sala: " + e.getMessage()); }
    }

    public void desvinculaSalaDeReserva(int salaId) {
        if (salaId == 0) return;
        String sql = "UPDATE salas SET reserva_id = -1, estado = 'LIVRE' WHERE id = ?";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, salaId); statement.executeUpdate();
        } catch (SQLException e) { System.out.println("Erro ao libertar sala: " + e.getMessage()); }
    }

    public void atualizarTodos(List<Reserva> reservas) {
        String sql = "UPDATE reservas SET estado = ? WHERE id = ?";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            try {
                for (Reserva reserva : reservas) {
                    statement.setString(1, reserva.getEstadoReserva().name());
                    statement.setInt(2, reserva.getId());
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) { System.out.println("Erro ao atualizar estados de reservas: " + e.getMessage()); }
    }
}
