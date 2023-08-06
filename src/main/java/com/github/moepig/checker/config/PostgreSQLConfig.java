package com.github.moepig.checker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "postgresql")
public class PostgreSQLConfig {
    private String host;
    private String port;
    private String schema;
    private String username;
    private String password;
}
