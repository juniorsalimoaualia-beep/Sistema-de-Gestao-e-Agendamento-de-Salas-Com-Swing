package com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mycompany.sistemadegestaoeagendamentodesalas.dao.CursoDAO;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Curso;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Estudante;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Turma;

public class TurmaDAO {
    private final EstudanteDAO estudanteDAO = new EstudanteDAO();
    private final CursoDAO cursoDAO = new CursoDAO();

    public void salvar(Turma dp){
        if (dp == null) {
            System.out.println("Erro: Turma invalida.");
            return;
        }
        Curso curso = cursoDAO.buscarCursoPorNome(dp.getCursoNome());
        if (curso == null || dp.getAno() <= 0) {
            System.out.println("Erro: Curso e ano validos sao obrigatorios para a turma.");
            return;
        }
        String sql = "INSERT INTO turmas(ano, curso_id) VALUES(?, ?) "
                + "ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement turma = connection.prepareStatement(sql);
             PreparedStatement remove = connection.prepareStatement("DELETE FROM turma_estudantes WHERE turma_id = ?");
             PreparedStatement estudante = connection.prepareStatement(
                     "INSERT IGNORE INTO turma_estudantes(turma_id, estudante_id) VALUES(?, ?)")) {
            connection.setAutoCommit(false);
            try {
                turma.setInt(1, dp.getAno());
                turma.setInt(2, curso.getId());
                turma.executeUpdate();

                int turmaId = buscarId(connection, dp.getAno(), curso.getId());
                remove.setInt(1, turmaId);
                remove.executeUpdate();
                for (Estudante aluno : dp.getEstudantes()) {
                    estudante.setInt(1, turmaId);
                    estudante.setInt(2, aluno.getId());
                    estudante.addBatch();
                }
                estudante.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao salvar turma: " + e.getMessage());
        }
    }

    private static int buscarId(Connection connection, int ano, int cursoId) throws SQLException {
        String sql = "SELECT id FROM turmas WHERE ano = ? AND curso_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ano);
            statement.setInt(2, cursoId);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt("id");
                }
            }
        }
        throw new SQLException("Nao foi possivel encontrar a turma guardada.");
    }

    static void associarEstudante(Connection connection, int estudanteId, int cursoId, int ano) throws SQLException {
        String criarTurma = "INSERT INTO turmas(ano, curso_id) VALUES(?, ?) "
                + "ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id)";
        try (PreparedStatement statement = connection.prepareStatement(criarTurma)) {
            statement.setInt(1, ano);
            statement.setInt(2, cursoId);
            statement.executeUpdate();
        }

        int turmaId = buscarId(connection, ano, cursoId);
        try (PreparedStatement removerTurmasAntigas = connection.prepareStatement(
                "DELETE FROM turma_estudantes WHERE estudante_id = ?");
             PreparedStatement adicionarNaTurma = connection.prepareStatement(
                     "INSERT INTO turma_estudantes(turma_id, estudante_id) VALUES(?, ?)")) {
            removerTurmasAntigas.setInt(1, estudanteId);
            removerTurmasAntigas.executeUpdate();
            adicionarNaTurma.setInt(1, turmaId);
            adicionarNaTurma.setInt(2, estudanteId);
            adicionarNaTurma.executeUpdate();
        }
    }

    public List<Turma> listaTurma(){
        List<Turma> lista=new ArrayList<>();
        String sql = "SELECT t.id, t.ano, c.id AS curso_id, c.nome AS curso_nome "
                + "FROM turmas t JOIN cursos c ON c.id = t.curso_id ORDER BY t.ano, c.nome";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                int turmaId = result.getInt("id");
                int ano = result.getInt("ano");
                Curso curso = new Curso(result.getInt("curso_id"), result.getString("curso_nome"));
                List<Estudante> estudantes = new ArrayList<>();
                String alunosSql = "SELECT estudante_id FROM turma_estudantes WHERE turma_id = ?";
                try (PreparedStatement alunos = connection.prepareStatement(alunosSql)) {
                    alunos.setInt(1, turmaId);
                    try (ResultSet rows = alunos.executeQuery()) {
                        while (rows.next()) {
                            Estudante estudante = estudanteDAO.buscarEstudantePorId(rows.getInt(1));
                            if (estudante != null) estudantes.add(estudante);
                        }
                    }
                }
                Turma turma = new Turma(turmaId, ano, curso, estudantes);
                lista.add(turma);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar turmas: " + e.getMessage());
        }
        return lista;
    }

    public String buscarPorTurma(String chave){
        List<Turma> lista=listaTurma();
        for(Turma dp:lista){
            if(chave != null && chave.equals(dp.getChave())){
                return dp.getCursoNome();
            }
        }
        return "Turma nao encontrado";
    }

    public boolean turmaExiste(String chave){
        if(chave == null || chave.trim().isEmpty()){
            return false;
        }
        for(Turma dp : listaTurma()){
            if(chave.equals(dp.getChave())){
                return true;
            }
        }
        return false;
    }

    public int gerarProximoId(){
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement("SELECT COALESCE(MAX(id), 0) + 1 FROM turmas");
             ResultSet result = statement.executeQuery()) { return result.next() ? result.getInt(1) : 1; }
        catch (SQLException e) {
            throw new IllegalStateException("Erro ao gerar o proximo ano da turma.", e);
        }
    }
}
