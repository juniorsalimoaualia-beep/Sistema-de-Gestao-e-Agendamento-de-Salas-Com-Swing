package com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Curso;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Estudante;


public class EstudanteDAO {
    private final CursoDAO cursoDAO = new CursoDAO();

    public boolean salvar(Estudante dp){
        if (dp == null || dp.getCurso() == null || dp.getCurso().getId() <= 0 || dp.getAno() <= 0) {
            System.out.println("Erro: Estudante, curso e ano validos sao obrigatorios.");
            return false;
        }
        if (isEstudanteDuplicado(dp)) {
            System.out.println("Erro: Estudante ja cadastrado.");
            return false;
        }
        String sql = "INSERT INTO usuarios(id, perfil, nome, apelido, curso_id, ano, celular, email, senha_hash) "
                + "VALUES(?, 'ESTUDANTE', ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            try {
                statement.setInt(1, dp.getId());
                statement.setString(2, dp.getNome());
                statement.setString(3, dp.getApelido());
                statement.setInt(4, dp.getCurso().getId());
                statement.setInt(5, dp.getAno());
                statement.setInt(6, dp.getNumCel());
                statement.setString(7, dp.getEmail());
                statement.setString(8, AutenticacaoDAO.prepararSenha(dp.getSenha()));
                statement.executeUpdate();

                TurmaDAO.associarEstudante(connection, dp.getId(), dp.getCurso().getId(), dp.getAno());
                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao salvar estudante: " + e.getMessage());
            return false;
        }
    }

    public List<Estudante> listaEstudante(){
        List<Estudante> lista=new ArrayList<>();
        String sql = "SELECT id, nome, apelido, curso_id, ano, celular, email, senha_hash "
                + "FROM usuarios WHERE perfil = 'ESTUDANTE' ORDER BY id";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                Curso curso = cursoDAO.buscarCursoPorId(result.getInt("curso_id"));
                lista.add(new Estudante(result.getInt("id"), result.getString("nome"), result.getString("apelido"),
                        curso, result.getInt("ano"), result.getInt("celular"), result.getString("email"),
                        result.getString("senha_hash")));
            }
        } catch (SQLException e) { System.out.println("Erro ao listar estudantes: " + e.getMessage()); }
        return lista;
    }

    public Estudante buscarEstudantePorId(int id){
        String sql = "SELECT id, nome, apelido, curso_id, ano, celular, email, senha_hash "
                + "FROM usuarios WHERE perfil = 'ESTUDANTE' AND id = ?";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    Curso curso = cursoDAO.buscarCursoPorId(result.getInt("curso_id"));
                    return new Estudante(result.getInt("id"), result.getString("nome"), result.getString("apelido"),
                            curso, result.getInt("ano"), result.getInt("celular"), result.getString("email"),
                            result.getString("senha_hash"));
                }
            }
        } catch (SQLException e) { System.out.println("Erro ao procurar estudante: " + e.getMessage()); }
        return null;
    }

    public String buscarPorEstudante(int id){
        Estudante estudante = buscarEstudantePorId(id);
        if (estudante != null) return estudante.getNome();
        return "Estudante nao encontrado";
    }

    public int gerarProximoId(){
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COALESCE(MAX(id), 0) + 1 FROM usuarios");
             ResultSet result = statement.executeQuery()) {
            return result.next() ? result.getInt(1) : 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao gerar ID do estudante: " + e.getMessage(), e);
        }
    }

    public boolean isEstudanteDuplicado(Estudante novo){
        if (novo == null) {
            return false;
        }
        String sql = "SELECT 1 FROM usuarios WHERE perfil = 'ESTUDANTE' AND "
                + "(LOWER(email) = LOWER(?) OR (LOWER(CONCAT(nome, ' ', apelido)) = LOWER(?) "
                + "AND ((curso_id IS NULL AND ? IS NULL) OR curso_id = ?) AND ano = ?)) LIMIT 1";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, novo.getEmail());
            statement.setString(2, novo.getNomeCompleto());
            if (novo.getCurso() == null) {
                statement.setNull(3, java.sql.Types.INTEGER);
                statement.setNull(4, java.sql.Types.INTEGER);
            } else {
                statement.setInt(3, novo.getCurso().getId());
                statement.setInt(4, novo.getCurso().getId());
            }
            statement.setInt(5, novo.getAno());
            try (ResultSet result = statement.executeQuery()) { return result.next(); }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao verificar se o estudante ja existe.", e);
        }
    }

    public boolean alterarSenha(int id, String senhaAtual, String senhaNova) {
        Estudante estudante = buscarEstudantePorId(id);
        if (estudante == null || !AutenticacaoDAO.validarHash(senhaAtual, estudante.getSenha())) return false;
        String sql = "UPDATE usuarios SET senha_hash = ? WHERE id = ? AND perfil = 'ESTUDANTE'";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, AutenticacaoDAO.criarHash(senhaNova));
            statement.setInt(2, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) { System.out.println("Erro ao alterar senha: " + e.getMessage()); return false; }
    }

    public void atualizarTodos(List<Estudante> lista) {
        String delete = "DELETE FROM usuarios WHERE perfil = 'ESTUDANTE'";
        String limparTurmas = "DELETE FROM turma_estudantes";
        String insert = "INSERT INTO usuarios(id, perfil, nome, apelido, curso_id, ano, celular, email, senha_hash) "
                + "VALUES(?, 'ESTUDANTE', ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement remove = connection.prepareStatement(delete);
             PreparedStatement removeTurmas = connection.prepareStatement(limparTurmas);
             PreparedStatement add = connection.prepareStatement(insert)) {
            connection.setAutoCommit(false);
            try {
                removeTurmas.executeUpdate();
                remove.executeUpdate();
                for (Estudante estudante : lista) {
                    add.setInt(1, estudante.getId());
                    add.setString(2, estudante.getNome());
                    add.setString(3, estudante.getApelido());
                    if (estudante.getCurso() == null || estudante.getCurso().getId() == 0) {
                        add.setNull(4, java.sql.Types.INTEGER);
                    } else {
                        add.setInt(4, estudante.getCurso().getId());
                    }
                    add.setInt(5, estudante.getAno());
                    add.setInt(6, estudante.getNumCel());
                    add.setString(7, estudante.getEmail());
                    add.setString(8, AutenticacaoDAO.prepararSenha(estudante.getSenha()));
                    add.executeUpdate();
                    if (estudante.getCurso() != null && estudante.getCurso().getId() > 0
                            && estudante.getAno() > 0) {
                        TurmaDAO.associarEstudante(connection, estudante.getId(),
                                estudante.getCurso().getId(), estudante.getAno());
                    }
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar estudantes: " + e.getMessage());
        }
    }
}
