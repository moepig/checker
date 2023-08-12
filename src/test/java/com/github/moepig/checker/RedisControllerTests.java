package com.github.moepig.checker;

import com.github.moepig.checker.config.RedisConfig;
import com.github.moepig.checker.json.RedisController;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@Tag("controller")
@SpringBootTest
public class RedisControllerTests {
    @Test
    void validConfig() {
        var config = new RedisConfig();
        config.setHost("localhost");
        config.setPort("6379");
        config.setUsername("");
        config.setPassword("");

        var controller = new RedisController(config);
        var responseEntity = controller.check();
        var response = responseEntity.getBody();

        assertNotNull(response);

        // レスポンスボディに変換されるオブジェクトの検査
        assertTrue(response.isOk());
        assertNotNull(response.getDnsResolveResult());
        assertNotNull(response.getInfo());
        assertNotEquals("", response.getInfo());
        assertEquals(config, response.getConfig());

        // HTTP の取り扱いの検査
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void invalidConfig_invalidHost() {
        var config = new RedisConfig();
        config.setHost("example");  // 存在しないホスト
        config.setPort("3306");
        config.setUsername("");
        config.setPassword("");

        var controller = new RedisController(config);
        var responseEntity = controller.check();
        var response = responseEntity.getBody();

        assertNotNull(response);

        // レスポンスボディに変換されるオブジェクトの検査
        assertFalse(response.isOk());
        assertNull(response.getDnsResolveResult());
        assertNull(response.getInfo());
        assertEquals(config, response.getConfig());

        // HTTP の取り扱いの検査
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void invalidConfig_invalidConnection() {
        var config = new RedisConfig();
        config.setHost("localhost");
        config.setPort("6380");  // 異なるポート
        config.setUsername("");
        config.setPassword("");

        var controller = new RedisController(config);
        var responseEntity = controller.check();
        var response = responseEntity.getBody();

        assertNotNull(response);

        // レスポンスボディに変換されるオブジェクトの検査
        assertFalse(response.isOk());
        assertNotNull(response.getDnsResolveResult());
        assertNull(response.getInfo());
        assertEquals(config, response.getConfig());

        // HTTP の取り扱いの検査
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}
