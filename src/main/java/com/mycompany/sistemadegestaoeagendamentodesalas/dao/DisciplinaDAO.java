package com.mycompany.sistemadegestaoeagendamentodesalas.dao;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Curso;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Disciplina;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Docente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class DisciplinaDAO {
    private DocenteDAO docenteDAO = new DocenteDAO();
    private CursoDAO cursoDAO = new CursoDAO();

    public void salvar(Disciplina ds){
        String sql = "INSERT INTO disciplinas(id, nome, docente_id, curso_id) VALUES(?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE nome = VALUES(nome), docente_id = VALUES(docente_id), "
                + "curso_id = VALUES(curso_id)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ds.getId());
            statement.setString(2, ds.getNome());
            if (ds.getDocente() == null) statement.setNull(3, java.sql.Types.INTEGER);
            else statement.setInt(3, ds.getDocente().getId());
            if (ds.getCurso() == null || ds.getCurso().getId() == 0) statement.setNull(4, java.sql.Types.INTEGER);
            else statement.setInt(4, ds.getCurso().getId());
            statement.executeUpdate();
        } catch (SQLException e) { System.out.println("Erro ao salvar disciplina: " + e.getMessage()); }
    }

    public List<Disciplina> listaDisciplina(){
        List<Disciplina> lista= new ArrayList<>();
        String sql = "SELECT id, nome, docente_id, curso_id FROM disciplinas ORDER BY id";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) lista.add(lerDisciplina(result));
        } catch (SQLException e) {
            System.out.println("Erro ao listar disciplinas: " + e.getMessage());
        }
        return lista;
    }

    private Disciplina lerDisciplina(ResultSet result) throws SQLException {
        Docente docente = docenteDAO.buscarPorDocente(result.getInt("docente_id"));
        Curso curso = cursoDAO.buscarCursoPorId(result.getInt("curso_id"));
        return new Disciplina(result.getInt("id"), result.getString("nome"), docente,
                curso == null ? new Curso(0, "") : curso);
    }

    public List<Disciplina> listarPorDocente(Docente docente) {
        return docente == null ? new ArrayList<>() : listarComFiltro("docente_id", docente.getId());
    }

    public List<Disciplina> listarPorCurso(String curso) {
        Curso encontrado = cursoDAO.buscarCursoPorNome(curso);
        return encontrado == null ? new ArrayList<>() : listarComFiltro("curso_id", encontrado.getId());
    }

    private List<Disciplina> listarComFiltro(String coluna, int valor) {
        List<Disciplina> lista = new ArrayList<>();
        String sql = "SELECT id, nome, docente_id, curso_id FROM disciplinas WHERE " + coluna + " = ? ORDER BY id";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, valor);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) lista.add(lerDisciplina(result));
            }
        } catch (SQLException e) { System.out.println("Erro ao filtrar disciplinas: " + e.getMessage()); }
        return lista;
    }

    public Disciplina buscarPorId(int id){
        String sql = "SELECT id, nome, docente_id, curso_id FROM disciplinas WHERE id = ?";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) return lerDisciplina(result);
            }
        } catch (SQLException e) { System.out.println("Erro ao procurar disciplina: " + e.getMessage()); }
        return null;
    }

    public int gerarProximoId(){
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement("SELECT COALESCE(MAX(id), 0) + 1 FROM disciplinas");
             ResultSet result = statement.executeQuery()) {
            return result.next() ? result.getInt(1) : 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao gerar ID da disciplina.", e);
        }
    }

    public Disciplina buscarPorNome(String nome) {
        if (nome == null) return null;
        String sql = "SELECT id, nome, docente_id, curso_id FROM disciplinas WHERE lower(nome) = lower(?) LIMIT 1";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nome.trim());
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) return lerDisciplina(result);
            }
        } catch (SQLException e) { System.out.println("Erro ao procurar disciplina: " + e.getMessage()); }
        return null;
    }

    public void atualizarTodos(List<Disciplina> lista) {
        String sql = "INSERT INTO disciplinas(id, nome, docente_id, curso_id) VALUES(?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE nome = VALUES(nome), docente_id = VALUES(docente_id), "
                + "curso_id = VALUES(curso_id)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            try {
                for (Disciplina disciplina : lista) {
                    statement.setInt(1, disciplina.getId());
                    statement.setString(2, disciplina.getNome());
                    if (disciplina.getDocente() == null) statement.setNull(3, java.sql.Types.INTEGER);
                    else statement.setInt(3, disciplina.getDocente().getId());
                    if (disciplina.getCurso() == null || disciplina.getCurso().getId() == 0) {
                        statement.setNull(4, java.sql.Types.INTEGER);
                    } else {
                        statement.setInt(4, disciplina.getCurso().getId());
                    }
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) { System.out.println("Erro ao guardar disciplinas: " + e.getMessage()); }
    }
}
