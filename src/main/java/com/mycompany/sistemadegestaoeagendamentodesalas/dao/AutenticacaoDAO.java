package com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class AutenticacaoDAO {
    public boolean autenticarLogin(String perfil, String email, String senha){
        return autenticarUtilizador(perfil, email, senha);
    }

    public int buscarIdUtilizador(String perfil, String email) {
        String query = "SELECT id FROM usuarios WHERE perfil = ? AND lower(email) = lower(?)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, perfil.toUpperCase());
            statement.setString(2, email.trim());
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getInt("id") : 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Nao foi possivel localizar a conta autenticada.", e);
        }
    }

    public boolean existeAdministrador() {
        String query = "SELECT 1 FROM usuarios WHERE perfil = 'ADMIN' LIMIT 1";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet result = statement.executeQuery()) {
            return result.next();
        } catch (SQLException e) {
            throw new IllegalStateException("Nao foi possivel consultar os administradores.", e);
        }
    }

    public void criarAdministrador(String email, String senha) {
        if (email == null || email.isBlank() || senha == null || senha.isEmpty()) {
            throw new IllegalArgumentException("O email e a senha sao obrigatorios.");
        }
        String query = "INSERT INTO usuarios(perfil, nome, apelido, celular, email, senha_hash) "
                + "VALUES('ADMIN', 'Administrador', '', 0, ?, ?)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email.trim().toLowerCase());
            statement.setString(2, criarHash(senha));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Nao foi possivel criar o administrador.", e);
        }
    }

    private boolean autenticarUtilizador(String perfil, String email, String senha) {
        if (perfil == null || email == null || senha == null) {
            return false;
        }
        String query = "SELECT id, senha_hash FROM usuarios WHERE perfil = ? AND lower(email) = lower(?)";
        try (Connection connection = ConexaoSql.conectar();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, perfil.toUpperCase());
            statement.setString(2, email.trim());
            int id;
            String senhaGuardada;
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return false;
                }
                id = result.getInt("id");
                senhaGuardada = result.getString("senha_hash");
            }
            if (!validarHash(senha, senhaGuardada)) {
                return false;
            }
            if (senhaGuardada.startsWith("120000:")) {
                atualizarHash(connection, id, senhaGuardada, criarHash(senha));
            }
            return true;
        } catch (SQLException e) {
            throw new IllegalStateException("Nao foi possivel validar as credenciais.", e);
        }
    }

    private void atualizarHash(Connection connection, int id, String hashAntigo, String hashNovo)
            throws SQLException {
        String query = "UPDATE usuarios SET senha_hash = ? WHERE id = ? AND senha_hash = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, hashNovo);
            statement.setInt(2, id);
            statement.setString(3, hashAntigo);
            statement.executeUpdate();
        }
    }

    static String criarHash(String senha) {
        return BCrypt.hashpw(senha == null ? "" : senha, BCrypt.gensalt(12));
    }

    static String prepararSenha(String senha) {
        if (senha != null && (senha.startsWith("$2a$") || senha.startsWith("$2b$")
                || senha.startsWith("120000:"))) {
            return senha;
        }
        return criarHash(senha);
    }

    static boolean validarHash(String senha, String valorGuardado) {
        if (senha == null || valorGuardado == null) {
            return false;
        }
        if (valorGuardado.startsWith("$2a$") || valorGuardado.startsWith("$2b$")) {
            try {
                return BCrypt.checkpw(senha, valorGuardado);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return validarHashPbkdf2(senha, valorGuardado);
    }

    private static boolean validarHashPbkdf2(String senha, String valorGuardado) {
        String[] partes = valorGuardado.split(":", 3);
        try {
            if (partes.length != 3) {
                return false;
            }
            int iteracoes = Integer.parseInt(partes[0]);
            byte[] salt = Base64.getDecoder().decode(partes[1]);
            byte[] hashGuardado = Base64.getDecoder().decode(partes[2]);
            return MessageDigest.isEqual(hashGuardado, hash(salt, senha.toCharArray(), iteracoes));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return false;
        }
    }

    private static byte[] hash(byte[] salt, char[] senha, int iteracoes) {
        PBEKeySpec spec = new PBEKeySpec(senha, salt, iteracoes, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("PBKDF2 nao esta disponivel.", e);
        } finally {
            spec.clearPassword();
            java.util.Arrays.fill(senha, '\0');
        }
    }

}
