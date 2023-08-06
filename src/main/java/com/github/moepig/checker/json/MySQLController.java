package com.github.moepig.checker.json;

import com.github.moepig.checker.config.MySQLConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@RestController
@RequestMapping("/json/mysql")
public class MySQLController {
    private static final Logger _logger = LoggerFactory.getLogger(MySQLController.class);

    private final MySQLConfig _config;

    @Autowired
    public MySQLController(MySQLConfig config) {
        _config = config;
    }

    @GetMapping("")
    public MySQLResponse greeting() {
        var responseData = new MySQLResponse();

        responseData.setConfig(_config);

        // 名前解決のチェック
        try {
            var address = InetAddress.getByName(_config.getHost());

            responseData.setDnsResolveResult(address.getHostAddress());
        } catch (UnknownHostException e) {
            _logger.error("dns resolve error", e);
            responseData.setOk(false);

            return responseData;
        }

        // 実プロトコルでの接続チェック
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + _config.getHost() + ":" + _config.getPort() + "/" + _config.getSchema() + "?useSSL=false&allowPublicKeyRetrieval=true",
                    _config.getUsername(),
                    _config.getPassword()
            );

            var metadata = connection.getMetaData();

            responseData.setDatabaseName(metadata.getDatabaseProductName());
            responseData.setDatabaseVersion(metadata.getDatabaseProductVersion());
        } catch (SQLException e) {
            _logger.error("mysql connection or query error", e);
            responseData.setOk(false);

            return responseData;
        }

        // 早期リターンしてなかったら成功扱い
        responseData.setOk(true);

        return responseData;
    }
}

