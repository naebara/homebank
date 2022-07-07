package com.bank.devprofile;

import com.bank.BankApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Objects;

import static org.junit.Assert.assertThrows;

public class DevProfileTest {

    @Value("${spring.profiles.active}")
    String profile;

    @Test

    public void testConnectingToPostgres_shouldFailToConnect() {
        BeanCreationException ex = assertThrows(BeanCreationException.class, () -> {
            new SpringApplicationBuilder(BankApplication.class)
                    .profiles("dev")
                    .run("-Dspring.profiles.active=dev");
        });

        Assertions.assertTrue(Objects.requireNonNull(ex.getMessage()).startsWith("Error creating bean with name" +
                " 'initializer' defined in class path resource [com/bank/config/DatabaseConnectionConfiguration.class]:" +
                " Invocation of init method failed; nested exception is org.springframework.data.r2dbc.connectionfactor" +
                "y.init.ScriptStatementFailedException: Failed to execute SQL script statement #3 of class path re" +
                "source [data.sql]: create table customer ( id bigint a"));


    }
}
