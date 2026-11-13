package com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;

public class ConexaoSql {
    private static final Properties CONFIG = carregarConfig();

    private static Properties carregarConfig(){
        Properties props = new Properties();
        try(FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
        } catch (Exception e) {
            System.err.println("Erro ao carregar o arquivo de configuração: " + e.getMessage());
        }
        return props;
    }

    public static Connection conectar() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do MySQL nao encontrado no classpath. " + e.getMessage(), e);
        }

        return DriverManager.getConnection(
            CONFIG.getProperty("db.url"),
            CONFIG.getProperty("db.user"),
            CONFIG.getProperty("db.password")
        );
    }
}
