package com.mycompany.sistemadegestaoeagendamentodesalas.dao;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Departamento;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Curso;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Sala;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;


public class DepartamentoDAO {
    public void salvar(Departamento dp){
        String sql = "INSERT INTO departamentos(id, nome, curso_id, sala_id) VALUES(?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE nome = VALUES(nome), curso_id = VALUES(curso_id), sala_id = VALUES(sala_id)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, dp.getId());
            statement.setString(2, dp.getNome());
            if (dp.getCurso() == null || dp.getCurso().getId() == 0) statement.setNull(3, java.sql.Types.INTEGER);
            else statement.setInt(3, dp.getCurso().getId());
            if (dp.getSala() == null || dp.getSala().getId() == 0) statement.setNull(4, java.sql.Types.INTEGER);
            else statement.setInt(4, dp.getSala().getId());
            statement.executeUpdate();
        } catch (SQLException e) { System.out.println("Erro ao salvar departamento: " + e.getMessage()); }
    }

    public List<Departamento> listaDepartamento(){
        List<Departamento> lista=new ArrayList<>();
        String sql = "SELECT id, nome FROM departamentos ORDER BY id";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) lista.add(new Departamento(result.getInt("id"), result.getString("nome")));
        } catch (SQLException e) { System.out.println("Erro ao listar departamentos: " + e.getMessage()); }
        return lista;
    }

    public Departamento buscarPorId(int id){
        String sql = "SELECT id, nome FROM departamentos WHERE id = ?";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) return new Departamento(result.getInt("id"), result.getString("nome"));
            }
        } catch (SQLException e) { System.out.println("Erro ao procurar departamento: " + e.getMessage()); }
        return null;
    }

    public String buscarPorDepartamento(int id){
        Departamento departamento = buscarPorId(id);
        return departamento != null ? departamento.getNome() : "Departamento nao encontrado";
    }

    public int gerarProximoId(){
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement("SELECT COALESCE(MAX(id), 0) + 1 FROM departamentos");
             ResultSet result = statement.executeQuery()) {
            return result.next() ? result.getInt(1) : 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao gerar ID do departamento.", e);
        }
    }
}
