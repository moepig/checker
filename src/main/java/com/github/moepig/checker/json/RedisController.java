package com.github.moepig.checker.json;

import com.github.moepig.checker.config.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@RequestMapping("/json/redis")
public class RedisController {
    private static final Logger _logger = LoggerFactory.getLogger(RedisController.class);

    private final RedisConfig _config;

    @Autowired
    public RedisController(RedisConfig config) {
        _config = config;
    }

    @GetMapping("")
    public ResponseEntity<RedisResponse> check() {
        var responseData = new RedisResponse();

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
        JedisPool pool;
        if (_config.getUsername() != null && !_config.getUsername().equals("")) {
            pool = new JedisPool(
                    _config.getHost(),
                    Integer.parseInt(_config.getPort()),
                    _config.getUsername(),
                    _config.getPassword()
            );
        } else {
            pool = new JedisPool(
                    _config.getHost(),
                    Integer.parseInt(_config.getPort())
            );
        }

        try (Jedis jedis = pool.getResource()) {
            var info = jedis.info();

            responseData.setInfo(info);
        } catch (Exception e) {
            _logger.error("Redis connection or query error", e);
            responseData.setOk(false);

            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 早期リターンしてなかったら成功扱い
        responseData.setOk(true);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}
