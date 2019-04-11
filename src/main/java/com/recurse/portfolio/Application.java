package com.recurse.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class Application {
    private static final List<String> REQUIRED_ENVIRONMENT_VARIABLES = List.of(
        "CLIENT_ID",
        "CLIENT_SECRET",
        "JDBC_DATABASE_URL",
        "JDBC_DATABASE_USERNAME",
        "JDBC_DATABASE_PASSWORD"
    );

    public static void main(String[] args) {
        verifyRequiredEnvironmentVariables();
        SpringApplication.run(Application.class, args);
    }

    private static void verifyRequiredEnvironmentVariables() {
        List<String> missingVariables = REQUIRED_ENVIRONMENT_VARIABLES.stream()
            .filter(k -> StringUtils.isEmpty(System.getenv(k)))
            .collect(Collectors.toList());
        if (!missingVariables.isEmpty()) {
            throw new IllegalStateException(
                "The application could not start because it requires several " +
                    "environment variables for configuration. " +
                    "The missing environment variable(s) are: " +
                    String.join(", ", missingVariables) +
                    ". Please set these environment variables, " +
                    "perhaps by sourcing your .env file; " +
                    "if you already have, update it to match " +
                    "the .env.template file."
            );
        }
    }
}
