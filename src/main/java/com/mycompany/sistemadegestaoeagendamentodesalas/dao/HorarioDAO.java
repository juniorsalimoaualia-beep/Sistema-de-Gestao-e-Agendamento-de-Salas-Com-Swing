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

public class HorarioDAO {
    private CursoDAO cursoDAO = new CursoDAO();
    private DisciplinaDAO disciplinaDAO = new DisciplinaDAO();

    public void salvar(Horario horario) {
        if (horario == null || horario.getCurso() == null || horario.getCurso().getId() <= 0
                || horario.getDiaSemana() == null || horario.getHoraInicio() == null
                || horario.getHoraFim() == null || !horario.getHoraFim().isAfter(horario.getHoraInicio())) {
            System.out.println("Erro: Curso, dia e intervalo de horas validos sao obrigatorios.");
            return;
        }
        String sql = "INSERT INTO horarios(id, curso_id, disciplina_id, dia_semana, hora_inicio, hora_fim) "
                + "VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (temHorarioSobreposto(connection, horario)) {
                System.out.println("Erro: O curso ja tem um horario sobreposto nesse dia.");
                return;
            }
            statement.setInt(1, horario.getId());
            if (horario.getCurso() == null || horario.getCurso().getId() == 0) statement.setNull(2, java.sql.Types.INTEGER);
            else statement.setInt(2, horario.getCurso().getId());
            if (horario.getDisciplina() == null || horario.getDisciplina().getId() == 0) statement.setNull(3, java.sql.Types.INTEGER);
            else statement.setInt(3, horario.getDisciplina().getId());
            statement.setString(4, horario.getDiaSemana().name());
            statement.setString(5, horario.getHoraInicio().toString());
            statement.setString(6, horario.getHoraFim().toString());
            statement.executeUpdate();
        } catch (SQLException e) { System.out.println("Erro ao salvar horario: " + e.getMessage()); }
    }

    public List<Horario> listaHorario() {
        List<Horario> lista = new ArrayList<>();
        String sql = "SELECT id, curso_id, disciplina_id, dia_semana, hora_inicio, hora_fim FROM horarios ORDER BY id";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) lista.add(lerHorario(result));
        } catch (SQLException e) { System.out.println("Erro ao listar horarios: " + e.getMessage()); }
        return lista;
    }

    private Horario lerHorario(ResultSet result) throws SQLException {
        Curso curso = cursoDAO.buscarCursoPorId(result.getInt("curso_id"));
        Disciplina disciplina = disciplinaDAO.buscarPorId(result.getInt("disciplina_id"));
        return new Horario(result.getInt("id"), curso, disciplina,
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
        String sql = "SELECT id, curso_id, disciplina_id, dia_semana, hora_inicio, hora_fim FROM horarios WHERE "
                + coluna + " = ? ORDER BY dia_semana, hora_inicio";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) lista.add(lerHorario(result));
            }
        } catch (SQLException e) { System.out.println("Erro ao filtrar horarios: " + e.getMessage()); }
        return lista;
    }

    public Horario buscarPorId(int id) {
        String sql = "SELECT id, curso_id, disciplina_id, dia_semana, hora_inicio, hora_fim FROM horarios WHERE id = ?";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) { if (result.next()) return lerHorario(result); }
        } catch (SQLException e) { System.out.println("Erro ao procurar horario: " + e.getMessage()); }
        return null;
    }

    private String formatarHorario(Horario hr) {
        String nomeCurso = hr.getCurso() != null ? hr.getCurso().getNome() : "";
        String nomeDisciplina = hr.getDisciplina() != null ? hr.getDisciplina().getNome() : "";
        return "Curso: " + nomeCurso
            + " | Disciplina: " + nomeDisciplina
            + " | Dia: " + hr.getDiaSemana()
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

    private boolean temHorarioSobreposto(Connection connection, Horario novo) throws SQLException {
        String sql = "SELECT 1 FROM horarios WHERE curso_id = ? AND dia_semana = ? "
                + "AND hora_inicio < ? AND hora_fim > ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, novo.getCurso().getId());
            statement.setString(2, novo.getDiaSemana().name());
            statement.setString(3, novo.getHoraFim().toString());
            statement.setString(4, novo.getHoraInicio().toString());
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }
}
