package com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoSql {
    private static final String URL_PADRAO = "jdbc:mysql://localhost:3306/sgsas?useSSL=false&allowPublicKeyRetrieval=true"
            + "&serverTimezone=UTC&characterEncoding=UTF-8";

    private static String obterConfiguracao(String propriedade, String variavelAmbiente, String valorPadrao) {
        String valorSistema = System.getProperty(propriedade);
        if (valorSistema != null && !valorSistema.isBlank()) {
            return valorSistema;
        }

        String valorAmbiente = System.getenv(variavelAmbiente);
        if (valorAmbiente != null && !valorAmbiente.isBlank()) {
            return valorAmbiente;
        }

        return valorPadrao;
    }

    public static String obterUrl() {
        return obterConfiguracao("sgsas.db.url", "SGSAS_DB_URL", URL_PADRAO);
    }

    public static String obterUsuario() {
        return obterConfiguracao("sgsas.db.user", "SGSAS_DB_USER", "root");
    }

    public static String obterSenha() {
        return obterConfiguracao("sgsas.db.password", "SGSAS_DB_PASSWORD", "");
    }

    public static Connection conectar() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do MySQL nao encontrado no classpath. " + e.getMessage(), e);
        }

        return DriverManager.getConnection(obterUrl(), obterUsuario(), obterSenha());
    }
}
