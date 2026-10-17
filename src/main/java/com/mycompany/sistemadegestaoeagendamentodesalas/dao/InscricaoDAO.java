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

    public void salvar(Inscricao inscricao) {
        String sql = "INSERT IGNORE INTO inscricoes(id, estudante_id, disciplina_id) VALUES(?, ?, ?)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, inscricao.getId());
            statement.setInt(2, inscricao.getEstudante().getId());
            statement.setInt(3, inscricao.getDisciplina().getId());
            statement.executeUpdate();
        } catch (SQLException e) { System.out.println("Erro ao salvar inscricao: " + e.getMessage()); }
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
