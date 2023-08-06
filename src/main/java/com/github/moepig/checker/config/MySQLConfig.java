package com.github.moepig.checker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "mysql")
public class MySQLConfig {
    private String host;
    private String port;
    private String schema;
    private String username;
    private String password;
}
