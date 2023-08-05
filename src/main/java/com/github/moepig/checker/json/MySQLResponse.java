package com.github.moepig.checker.json;

import com.github.moepig.checker.config.MySQLConfig;
import lombok.Data;

import java.sql.DatabaseMetaData;
import java.util.List;

@Data
public class MySQLResponse {
    boolean ok;
    String dnsResolveResult;
    String databaseName;
    String databaseVersion;
    MySQLConfig config;
}

