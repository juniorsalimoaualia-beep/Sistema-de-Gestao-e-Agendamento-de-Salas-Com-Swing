package com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoSql {
    private static final String URL = System.getProperty("sgsas.db.url",
            System.getenv().getOrDefault("SGSAS_DB_URL",
                    "jdbc:mysql://localhost:3306/sgsas?useSSL=false&allowPublicKeyRetrieval=true"
                            + "&serverTimezone=UTC&characterEncoding=UTF-8"));
    private static final String USUARIO = System.getProperty("sgsas.db.user",
            System.getenv().getOrDefault("SGSAS_DB_USER", "root"));
    private static final String SENHA = System.getProperty("sgsas.db.password",
            System.getenv().getOrDefault("SGSAS_DB_PASSWORD", ""));

    public static Connection conectar() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do MySQL nao encontrado no classpath. " + e.getMessage(), e);
        }

        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
