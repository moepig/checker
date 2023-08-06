package com.github.moepig.checker;

import com.github.moepig.checker.config.MySQLConfig;
import com.github.moepig.checker.json.MySQLController;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@Tag("controller")
@SpringBootTest
public class MySQLControllerTests {
    @Test
    void validConfig() {
        var config = new MySQLConfig();
        config.setHost("localhost");
        config.setPort("3306");
        config.setSchema("mydatabase");
        config.setUsername("myuser");
        config.setPassword("secret");

        var controller = new MySQLController(config);
        var responseEntity = controller.check();
        var response = responseEntity.getBody();

        // レスポンスボディに変換されるオブジェクトの検査
        assertTrue(response.isOk());
        assertNotNull(response.getDnsResolveResult());
        assertNotNull(response.getDatabaseName());
        assertNotNull(response.getDatabaseVersion());
        assertTrue(response.getConfig().equals(config));

        // HTTP の取り扱いの検査
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void invalidConfig_invalidHost() {
        var config = new MySQLConfig();
        config.setHost("example");  // 存在しないホスト
        config.setPort("3306");
        config.setSchema("nodatabase");
        config.setUsername("nouser");
        config.setPassword("");

        var controller = new MySQLController(config);
        var responseEntity = controller.check();
        var response = responseEntity.getBody();

        // レスポンスボディに変換されるオブジェクトの検査
        assertFalse(response.isOk());
        assertNull(response.getDnsResolveResult());
        assertNull(response.getDatabaseName());
        assertNull(response.getDatabaseVersion());
        assertTrue(response.getConfig().equals(config));

        // HTTP の取り扱いの検査
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void invalidConfig_invalidConnection() {
        var config = new MySQLConfig();
        config.setHost("localhost");
        config.setPort("3306");
        config.setSchema("mydatabase");
        config.setUsername("nouser");  // 存在しないユーザ
        config.setPassword("");

        var controller = new MySQLController(config);
        var responseEntity = controller.check();
        var response = responseEntity.getBody();

        // レスポンスボディに変換されるオブジェクトの検査
        assertFalse(response.isOk());
        assertNotNull(response.getDnsResolveResult());
        assertNull(response.getDatabaseName());
        assertNull(response.getDatabaseVersion());
        assertTrue(response.getConfig().equals(config));

        // HTTP の取り扱いの検査
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}
