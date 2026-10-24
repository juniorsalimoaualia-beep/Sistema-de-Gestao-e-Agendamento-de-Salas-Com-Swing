package com.mycompany.sistemadegestaoeagendamentodesalas.dao;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Curso;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Departamento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class CursoDAO {
    private final DepartamentoDAO departamentoDAO = new DepartamentoDAO();

    private Departamento buscarDepartamento(int id) {
        return id <= 0 ? null : departamentoDAO.buscarPorId(id);
    }

    public void salvar(Curso cr){
        String sql = "INSERT INTO cursos(id, nome, departamento_id) VALUES(?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE nome = VALUES(nome), departamento_id = VALUES(departamento_id)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, cr.getId());
            statement.setString(2, cr.getNome());
            if (cr.getDepartamento() == null || cr.getDepartamento().getId() == 0) statement.setNull(3, java.sql.Types.INTEGER);
            else statement.setInt(3, cr.getDepartamento().getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao salvar curso.", e);
        }
    }

    public List<Curso> listaCurso(){
        List<Curso> lista=new ArrayList<>();
        String sql = "SELECT id, nome, departamento_id FROM cursos ORDER BY id";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                int departamentoId = result.getInt("departamento_id");
                Departamento departamento = result.wasNull() ? null : buscarDepartamento(departamentoId);
                lista.add(new Curso(result.getInt("id"), result.getString("nome"), departamento));
            }
        } catch (SQLException e) { System.out.println("Erro ao listar cursos: " + e.getMessage()); }
        return lista;
    }

    public Curso buscarCursoPorNome(String nome){
        if (nome == null) {
            return null;
        }
        String sql = "SELECT id, nome, departamento_id FROM cursos WHERE lower(nome) = lower(?) LIMIT 1";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nome.trim());
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) return new Curso(result.getInt("id"), result.getString("nome"));
            }
        } catch (SQLException e) { System.out.println("Erro ao procurar curso: " + e.getMessage()); }
        return null;
    }

    public int gerarProximoId(){
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement("SELECT COALESCE(MAX(id), 0) + 1 FROM cursos");
             ResultSet result = statement.executeQuery()) {
            return result.next() ? result.getInt(1) : 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao gerar ID do curso.", e);
        }
    }

    public Curso buscarCursoPorId(int id) {
        String sql = "SELECT id, nome, departamento_id FROM cursos WHERE id = ?";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    int departamentoId = result.getInt("departamento_id");
                    Departamento departamento = result.wasNull() ? null : buscarDepartamento(departamentoId);
                    return new Curso(result.getInt("id"), result.getString("nome"), departamento);
                }
            }
        } catch (SQLException e) { System.out.println("Erro ao procurar curso: " + e.getMessage()); }
        return null;
    }

    public void editarCurso(int id, String novoNome) {
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement("UPDATE cursos SET nome = ? WHERE id = ?")) {
            statement.setString(1, novoNome);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) { System.out.println("Erro ao editar curso: " + e.getMessage()); }
    }

    public void atualizarTodos(List<Curso> lista) {
        String sql = "INSERT INTO cursos(id, nome, departamento_id) VALUES(?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE nome = VALUES(nome), departamento_id = VALUES(departamento_id)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            try {
                for (Curso curso : lista) {
                    statement.setInt(1, curso.getId());
                    statement.setString(2, curso.getNome());
                    if (curso.getDepartamento() == null) statement.setNull(3, java.sql.Types.INTEGER);
                    else statement.setInt(3, curso.getDepartamento().getId());
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) { System.out.println("Erro ao guardar cursos: " + e.getMessage()); }
    }
}
