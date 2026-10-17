package com.mycompany.sistemadegestaoeagendamentodesalas.dao;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "SGSAS_TEST_DB_URL", matches = ".+")
class ConexaoSqlTest {
    @BeforeAll
    static void configurarBaseDeTeste() {
        System.setProperty("sgsas.db.url", System.getenv("SGSAS_TEST_DB_URL"));
        setPropertyFromEnvironment("sgsas.db.user", "SGSAS_TEST_DB_USER");
        setPropertyFromEnvironment("sgsas.db.password", "SGSAS_TEST_DB_PASSWORD");
    }

    private static void setPropertyFromEnvironment(String property, String environmentVariable) {
        String value = System.getenv(environmentVariable);
        if (value != null) {
            System.setProperty(property, value);
        }
    }

    @Test
    void conectaAoMySql() throws Exception {
        try (Connection connection = ConexaoSql.conectar()) {
            assertTrue(connection.isValid(2));
        }
    }
}
