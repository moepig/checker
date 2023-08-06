package com.github.moepig.checker.json;

import com.github.moepig.checker.config.PostgreSQLConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.DriverManager;
import java.sql.SQLException;

@RestController
@RequestMapping("/json/postgresql")
public class PostgreSQLController {
    private static final Logger _logger = LoggerFactory.getLogger(PostgreSQLController.class);

    private final PostgreSQLConfig _config;

    @Autowired
    public PostgreSQLController(PostgreSQLConfig config) {
        _config = config;
    }

    @GetMapping("")
    public ResponseEntity<PostgreSQLResponse> check() {
        var responseData = new PostgreSQLResponse();

        responseData.setConfig(_config);

        // 名前解決のチェック
        try {
            var address = InetAddress.getByName(_config.getHost());

            responseData.setDnsResolveResult(address.getHostAddress());
        } catch (UnknownHostException e) {
            _logger.error("dns resolve error", e);
            responseData.setOk(false);

            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 実プロトコルでの接続チェック
        try {
            var connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + _config.getHost() + ":" + _config.getPort() + "/" + _config.getSchema(),
                    _config.getUsername(),
                    _config.getPassword()
            );

            var metadata = connection.getMetaData();

            responseData.setDatabaseName(metadata.getDatabaseProductName());
            responseData.setDatabaseVersion(metadata.getDatabaseProductVersion());
        } catch (SQLException e) {
            _logger.error("postgresql connection or query error", e);
            responseData.setOk(false);

            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 早期リターンしてなかったら成功扱い
        responseData.setOk(true);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}
