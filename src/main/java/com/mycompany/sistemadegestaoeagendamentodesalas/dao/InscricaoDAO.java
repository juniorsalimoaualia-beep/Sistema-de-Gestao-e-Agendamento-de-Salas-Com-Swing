package com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import com.mycompany.sistemadegestaoeagendamentodesalas.model.Inscricao;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Estudante;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Disciplina;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InscricaoDAO {
    private EstudanteDAO estudanteDAO = new EstudanteDAO();
    private DisciplinaDAO disciplinaDAO = new DisciplinaDAO();

    public boolean salvar(Inscricao inscricao) {
        if (inscricao == null || inscricao.getEstudante() == null || inscricao.getDisciplina() == null
                || inscricao.getEstudante().getId() <= 0 || inscricao.getDisciplina().getId() <= 0) {
            return false;
        }
        String sql = "INSERT INTO inscricoes(estudante_id, disciplina_id) VALUES(?, ?)";
        try (Connection connection = ConexaoSql.conectar()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement lock = connection.prepareStatement(
                        "SELECT id FROM usuarios WHERE id = ? AND perfil = 'ESTUDANTE' FOR UPDATE")) {
                    lock.setInt(1, inscricao.getEstudante().getId());
                    try (ResultSet result = lock.executeQuery()) {
                        if (!result.next()) {
                            connection.rollback();
                            return false;
                        }
                    }
                }
                try (PreparedStatement disciplina = connection.prepareStatement(
                        "SELECT d.id FROM disciplinas d JOIN usuarios e ON e.id = ? "
                                + "AND e.perfil = 'ESTUDANTE' WHERE d.id = ? "
                                + "AND (d.curso_id IS NULL OR d.curso_id = e.curso_id)")) {
                    disciplina.setInt(1, inscricao.getEstudante().getId());
                    disciplina.setInt(2, inscricao.getDisciplina().getId());
                    try (ResultSet result = disciplina.executeQuery()) {
                        if (!result.next()) {
                            connection.rollback();
                            return false;
                        }
                    }
                }
                try (PreparedStatement count = connection.prepareStatement(
                        "SELECT COUNT(*) FROM inscricoes WHERE estudante_id = ?")) {
                    count.setInt(1, inscricao.getEstudante().getId());
                    try (ResultSet result = count.executeQuery()) {
                        if (!result.next() || result.getInt(1) >= 7) {
                            connection.rollback();
                            return false;
                        }
                    }
                }
                try (PreparedStatement duplicate = connection.prepareStatement(
                        "SELECT 1 FROM inscricoes WHERE estudante_id = ? AND disciplina_id = ? LIMIT 1")) {
                    duplicate.setInt(1, inscricao.getEstudante().getId());
                    duplicate.setInt(2, inscricao.getDisciplina().getId());
                    try (ResultSet result = duplicate.executeQuery()) {
                        if (result.next()) {
                            connection.rollback();
                            return false;
                        }
                    }
                }
                try (PreparedStatement statement = connection.prepareStatement(sql,
                        java.sql.Statement.RETURN_GENERATED_KEYS)) {
                    statement.setInt(1, inscricao.getEstudante().getId());
                    statement.setInt(2, inscricao.getDisciplina().getId());
                    if (statement.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            inscricao.setId(generatedKeys.getInt(1));
                        }
                    }
                }
                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao salvar inscricao: " + e.getMessage());
            return false;
        }
    }

    public List<Inscricao> listaInscricao() {
        List<Inscricao> lista = new ArrayList<>();
        String sql = "SELECT id, estudante_id, disciplina_id FROM inscricoes ORDER BY id";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) lista.add(criarInscricao(result));
        } catch (SQLException e) {
            System.out.println("Erro ao listar inscricoes: " + e.getMessage());
        }
        return lista;
    }

    private Inscricao criarInscricao(ResultSet result) throws SQLException {
        Estudante estudante = estudanteDAO.buscarEstudantePorId(result.getInt("estudante_id"));
        Disciplina disciplina = disciplinaDAO.buscarPorId(result.getInt("disciplina_id"));
        return new Inscricao(result.getInt("id"), estudante, disciplina);
    }

    public Inscricao buscarPorInscricao(int id) {
        String sql = "SELECT id, estudante_id, disciplina_id FROM inscricoes WHERE id = ?";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) { if (result.next()) return criarInscricao(result); }
        } catch (SQLException e) { System.out.println("Erro ao procurar inscricao: " + e.getMessage()); }
        return null;
    }

    public List<Inscricao> buscarPorEstudante(int estudanteId) {
        return listarPorCampo("estudante_id", estudanteId);
    }

    public List<Inscricao> buscarPorDisciplina(int disciplinaId) {
        return listarPorCampo("disciplina_id", disciplinaId);
    }

    private List<Inscricao> listarPorCampo(String campo, int id) {
        List<Inscricao> lista = new ArrayList<>();
        String sql = "SELECT id, estudante_id, disciplina_id FROM inscricoes WHERE " + campo + " = ? ORDER BY id";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) lista.add(criarInscricao(result));
            }
        } catch (SQLException e) { System.out.println("Erro ao filtrar inscricoes: " + e.getMessage()); }
        return lista;
    }

    public int gerarProximoId(){
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement("SELECT COALESCE(MAX(id), 0) + 1 FROM inscricoes");
             ResultSet result = statement.executeQuery()) { return result.next() ? result.getInt(1) : 1; }
        catch (SQLException e) {
            throw new IllegalStateException("Erro ao gerar ID da inscricao.", e);
        }
    }
}
