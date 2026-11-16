package com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mycompany.sistemadegestaoeagendamentodesalas.model.Curso;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.DiaSemana;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Disciplina;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Horario;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Sala;

public class HorarioDAO {
    private CursoDAO cursoDAO = new CursoDAO();
    private DisciplinaDAO disciplinaDAO = new DisciplinaDAO();
    private SalaDAO salaDAO = new SalaDAO();

    public void salvar(Horario horario) {
        if (horario == null || horario.getCurso() == null || horario.getCurso().getId() <= 0
                || horario.getSala() == null || horario.getSala().getId() <= 0
                || horario.getDiaSemana() == null || horario.getHoraInicio() == null
                || horario.getHoraFim() == null || !horario.getHoraFim().isAfter(horario.getHoraInicio())) {
            throw new IllegalArgumentException("Curso, dia e intervalo de horas validos sao obrigatorios.");
        }
        String sql = "INSERT INTO horarios(id, curso_id, disciplina_id, sala_id, dia_semana, hora_inicio, hora_fim) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (temConflito(connection, horario, 0)) {
                throw new IllegalArgumentException("O horario entra em conflito com outra aula, sala ou docente.");
            }
            statement.setInt(1, horario.getId());
            if (horario.getCurso() == null || horario.getCurso().getId() == 0) statement.setNull(2, java.sql.Types.INTEGER);
            else statement.setInt(2, horario.getCurso().getId());
            if (horario.getDisciplina() == null || horario.getDisciplina().getId() == 0) statement.setNull(3, java.sql.Types.INTEGER);
            else statement.setInt(3, horario.getDisciplina().getId());
            if (horario.getSala() == null || horario.getSala().getId() == 0) statement.setNull(4, java.sql.Types.INTEGER);
            else statement.setInt(4, horario.getSala().getId());
            statement.setString(5, horario.getDiaSemana().name());
            statement.setString(6, horario.getHoraInicio().toString());
            statement.setString(7, horario.getHoraFim().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao salvar horario.", e);
        }
    }

    public boolean atualizar(Horario horario) {
        validarHorario(horario);
        String sql = "UPDATE horarios SET dia_semana = ?, hora_inicio = ?, hora_fim = ?, sala_id = ? "
                + "WHERE id = ?";
        try (Connection connection = ConexaoSql.conectar()) {
            connection.setAutoCommit(false);
            try (PreparedStatement existe = connection.prepareStatement(
                    "SELECT 1 FROM horarios WHERE id = ? FOR UPDATE")) {
                existe.setInt(1, horario.getId());
                try (ResultSet resultado = existe.executeQuery()) {
                    if (!resultado.next()) {
                        connection.rollback();
                        return false;
                    }
                }
            }
            if (temConflito(connection, horario, horario.getId())) {
                connection.rollback();
                throw new IllegalArgumentException(
                        "O novo horario entra em conflito com outra aula, sala ou docente.");
            }
            try (PreparedStatement atualizar = connection.prepareStatement(sql)) {
                atualizar.setString(1, horario.getDiaSemana().name());
                atualizar.setString(2, horario.getHoraInicio().toString());
                atualizar.setString(3, horario.getHoraFim().toString());
                atualizar.setInt(4, horario.getSala().getId());
                atualizar.setInt(5, horario.getId());
                boolean atualizado = atualizar.executeUpdate() == 1;
                connection.commit();
                return atualizado;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao alterar horario.", e);
        }
    }

    public boolean trocarHorarios(int primeiroId, int segundoId) {
        if (primeiroId <= 0 || segundoId <= 0 || primeiroId == segundoId) {
            throw new IllegalArgumentException("Selecione duas aulas diferentes para trocar.");
        }
        try (Connection connection = ConexaoSql.conectar()) {
            connection.setAutoCommit(false);
            Horario primeiro = buscarPorId(connection, primeiroId);
            Horario segundo = buscarPorId(connection, segundoId);
            if (primeiro == null || segundo == null) {
                connection.rollback();
                return false;
            }

            IntervaloTemporario temporario = encontrarIntervaloTemporario(connection, primeiro, segundo);
            atualizarIntervalo(connection, primeiroId, temporario.inicio, temporario.fim, temporario.dia);
            atualizarIntervalo(connection, segundoId, primeiro.getHoraInicio(), primeiro.getHoraFim(),
                    primeiro.getDiaSemana());
            atualizarIntervalo(connection, primeiroId, segundo.getHoraInicio(), segundo.getHoraFim(),
                    segundo.getDiaSemana());

            if (temConflito(connection, copiarIntervalo(primeiro, segundo), primeiroId)
                    || temConflito(connection, copiarIntervalo(segundo, primeiro), segundoId)) {
                connection.rollback();
                throw new IllegalArgumentException(
                        "A troca criaria conflito com outra aula, sala ou docente.");
            }
            connection.commit();
            return true;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao trocar os horarios das aulas.", e);
        }
    }

    public List<Horario> listaHorario() {
        List<Horario> lista = new ArrayList<>();
        String sql = "SELECT id, curso_id, disciplina_id, sala_id, dia_semana, hora_inicio, hora_fim FROM horarios ORDER BY id";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) lista.add(lerHorario(result));
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar horarios.", e);
        }
        return lista;
    }

    private Horario lerHorario(ResultSet result) throws SQLException {
        Curso curso = cursoDAO.buscarCursoPorId(result.getInt("curso_id"));
        Disciplina disciplina = disciplinaDAO.buscarPorId(result.getInt("disciplina_id"));
        Sala sala = salaDAO.buscarPorId(result.getInt("sala_id"));
        return new Horario(result.getInt("id"), curso, disciplina, sala,
                DiaSemana.valueOf(result.getString("dia_semana")),
                LocalTime.parse(result.getString("hora_inicio")), LocalTime.parse(result.getString("hora_fim")));
    }

    public String buscarPorHorario(String curso) {
        List<Horario> lista = listarPorCurso(curso);
        return lista.isEmpty() ? "Horario nao encontrado" : formatarHorario(lista.get(0));
    }

    public List<Horario> listarPorCurso(String curso) {
        Curso encontrado = cursoDAO.buscarCursoPorNome(curso);
        return encontrado == null ? new ArrayList<>() : listarFiltrado("curso_id", encontrado.getId());
    }

    public List<Horario> listarPorDisciplina(int disciplinaId) {
        return listarFiltrado("disciplina_id", disciplinaId);
    }

    private List<Horario> listarFiltrado(String coluna, int id) {
        List<Horario> lista = new ArrayList<>();
        String sql = "SELECT id, curso_id, disciplina_id, sala_id, dia_semana, hora_inicio, hora_fim FROM horarios WHERE "
                + coluna + " = ? ORDER BY dia_semana, hora_inicio";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) lista.add(lerHorario(result));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao filtrar horarios.", e);
        }
        return lista;
    }

    public Horario buscarPorId(int id) {
        String sql = "SELECT id, curso_id, disciplina_id, sala_id, dia_semana, hora_inicio, hora_fim FROM horarios WHERE id = ?";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) { if (result.next()) return lerHorario(result); }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao procurar horario.", e);
        }
        return null;
    }

    private String formatarHorario(Horario hr) {
        String nomeCurso = hr.getCurso() != null ? hr.getCurso().getNome() : "";
        String nomeDisciplina = hr.getDisciplina() != null ? hr.getDisciplina().getNome() : "";
        return "Curso: " + nomeCurso
            + " | Disciplina: " + nomeDisciplina
            + " | Ano: " + (hr.getDisciplina() == null ? "-" : hr.getDisciplina().getAno() + "º ano")
            + " | Dia: " + hr.getDiaSemana()
            + " | Sala: " + (hr.getSala() == null ? "Nao definida" : hr.getSala().getNome())
            + " | Inicio: " + hr.getHoraInicio()
            + " | Fim: " + hr.getHoraFim();
    }

    public int gerarProximoId() {
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement("SELECT COALESCE(MAX(id), 0) + 1 FROM horarios");
             ResultSet result = statement.executeQuery()) { return result.next() ? result.getInt(1) : 1; }
        catch (SQLException e) {
            throw new IllegalStateException("Erro ao gerar ID do horario.", e);
        }
    }

    private boolean temConflito(Connection connection, Horario novo, int excluirId) throws SQLException {
        String sql = "SELECT 1 FROM horarios h "
                + "LEFT JOIN disciplinas disciplina_existente ON disciplina_existente.id = h.disciplina_id "
                + "LEFT JOIN disciplinas disciplina_nova ON disciplina_nova.id = ? "
                + "WHERE h.id <> ? AND h.dia_semana = ? AND h.hora_inicio < ? AND h.hora_fim > ? "
                + "AND (h.curso_id = ? OR h.sala_id = ? OR "
                + "(disciplina_nova.docente_id IS NOT NULL "
                + "AND disciplina_nova.docente_id = disciplina_existente.docente_id)) LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (novo.getDisciplina() == null) statement.setNull(1, java.sql.Types.INTEGER);
            else statement.setInt(1, novo.getDisciplina().getId());
            statement.setInt(2, excluirId);
            statement.setString(3, novo.getDiaSemana().name());
            statement.setString(4, novo.getHoraFim().toString());
            statement.setString(5, novo.getHoraInicio().toString());
            statement.setInt(6, novo.getCurso().getId());
            statement.setInt(7, novo.getSala().getId());
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    private void validarHorario(Horario horario) {
        if (horario == null || horario.getId() <= 0 || horario.getCurso() == null
                || horario.getCurso().getId() <= 0 || horario.getSala() == null
                || horario.getSala().getId() <= 0 || horario.getDiaSemana() == null
                || horario.getHoraInicio() == null || horario.getHoraFim() == null
                || !horario.getHoraFim().isAfter(horario.getHoraInicio())) {
            throw new IllegalArgumentException("Aula, sala, dia e intervalo de horas validos sao obrigatorios.");
        }
    }

    private Horario buscarPorId(Connection connection, int id) throws SQLException {
        String sql = "SELECT id, curso_id, disciplina_id, sala_id, dia_semana, hora_inicio, hora_fim "
                + "FROM horarios WHERE id = ? FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? lerHorario(result) : null;
            }
        }
    }

    private IntervaloTemporario encontrarIntervaloTemporario(Connection connection,
            Horario primeiro, Horario segundo) throws SQLException {
        boolean[][] ocupados = new boolean[DiaSemana.values().length][1440];
        String sql = "SELECT dia_semana, hora_inicio, hora_fim FROM horarios "
                + "WHERE curso_id = ? AND id NOT IN (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, primeiro.getCurso().getId());
            statement.setInt(2, primeiro.getId());
            statement.setInt(3, segundo.getId());
            try (ResultSet resultado = statement.executeQuery()) {
                while (resultado.next()) {
                    int dia = DiaSemana.valueOf(resultado.getString("dia_semana")).ordinal();
                    LocalTime inicio = LocalTime.parse(resultado.getString("hora_inicio"));
                    LocalTime fim = LocalTime.parse(resultado.getString("hora_fim"));
                    int primeiroMinuto = inicio.getHour() * 60 + inicio.getMinute();
                    int ultimoMinuto = fim.getHour() * 60 + fim.getMinute()
                            + (fim.getSecond() > 0 || fim.getNano() > 0 ? 1 : 0);
                    for (int minuto = primeiroMinuto; minuto < Math.min(1440, ultimoMinuto); minuto++) {
                        ocupados[dia][minuto] = true;
                    }
                }
            }
        }
        for (int dia = 0; dia < ocupados.length; dia++) {
            for (int minuto = 0; minuto < 1439; minuto++) {
                if (!ocupados[dia][minuto]) {
                    return new IntervaloTemporario(DiaSemana.values()[dia],
                            LocalTime.of(minuto / 60, minuto % 60),
                            LocalTime.of((minuto + 1) / 60, (minuto + 1) % 60));
                }
            }
        }
        throw new IllegalArgumentException("Nao ha um intervalo livre para processar a troca.");
    }

    private void atualizarIntervalo(Connection connection, int id, LocalTime inicio,
            LocalTime fim, DiaSemana dia) throws SQLException {
        String sql = "UPDATE horarios SET dia_semana = ?, hora_inicio = ?, hora_fim = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, dia.name());
            statement.setString(2, inicio.toString());
            statement.setString(3, fim.toString());
            statement.setInt(4, id);
            statement.executeUpdate();
        }
    }

    private Horario copiarIntervalo(Horario destino, Horario origem) {
        return new Horario(destino.getId(), destino.getCurso(), destino.getDisciplina(),
                destino.getSala(), origem.getDiaSemana(), origem.getHoraInicio(), origem.getHoraFim());
    }

    private static final class IntervaloTemporario {
        private final DiaSemana dia;
        private final LocalTime inicio;
        private final LocalTime fim;

        private IntervaloTemporario(DiaSemana dia, LocalTime inicio, LocalTime fim) {
            this.dia = dia;
            this.inicio = inicio;
            this.fim = fim;
        }
    }
}
