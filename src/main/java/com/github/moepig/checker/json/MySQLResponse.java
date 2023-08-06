package com.github.moepig.checker.json;

import com.github.moepig.checker.config.MySQLConfig;
import lombok.Data;

@Data
public class MySQLResponse {
    boolean ok;
    String dnsResolveResult;
    String databaseName;
    String databaseVersion;
    MySQLConfig config;
}

