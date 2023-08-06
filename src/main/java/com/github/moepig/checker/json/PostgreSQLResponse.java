package com.github.moepig.checker.json;

import com.github.moepig.checker.config.PostgreSQLConfig;
import lombok.Data;

@Data
public class PostgreSQLResponse {
    boolean ok;
    String dnsResolveResult;
    String databaseName;
    String databaseVersion;
    PostgreSQLConfig config;
}
