package com.github.moepig.checker;

import com.github.moepig.checker.config.PostgreSQLConfig;
import com.github.moepig.checker.json.PostgreSQLController;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@Tag("controller")
@SpringBootTest
public class PostgreSQLControllerTests {
    @Test
    void validConfig() {
        var config = new PostgreSQLConfig();
        config.setHost("localhost");
        config.setPort("5432");
        config.setSchema("mydatabase");
        config.setUsername("myuser");
        config.setPassword("secret");

        var controller = new PostgreSQLController(config);
        var responseEntity = controller.check();
        var response = responseEntity.getBody();

        assertNotNull(response);

        // レスポンスボディに変換されるオブジェクトの検査
        assertTrue(response.isOk());
        assertNotNull(response.getDnsResolveResult());
        assertNotNull(response.getDatabaseName());
        assertNotNull(response.getDatabaseVersion());
        assertEquals(config, response.getConfig());

        // HTTP の取り扱いの検査
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void invalidConfig_invalidHost() {
        var config = new PostgreSQLConfig();
        config.setHost("example");  // 存在しないホスト
        config.setPort("5432");
        config.setSchema("mydatabase");
        config.setUsername("myuser");
        config.setPassword("secret");

        var controller = new PostgreSQLController(config);
        var responseEntity = controller.check();
        var response = responseEntity.getBody();

        assertNotNull(response);

        // レスポンスボディに変換されるオブジェクトの検査
        assertFalse(response.isOk());
        assertNull(response.getDnsResolveResult());
        assertNull(response.getDatabaseName());
        assertNull(response.getDatabaseVersion());
        assertEquals(config, response.getConfig());

        // HTTP の取り扱いの検査
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void invalidConfig_invalidConnection() {
        var config = new PostgreSQLConfig();
        config.setHost("localhost");
        config.setPort("5432");
        config.setSchema("mydatabase");
        config.setUsername("nouser");  // 存在しないユーザ
        config.setPassword("nopassword");

        var controller = new PostgreSQLController(config);
        var responseEntity = controller.check();
        var response = responseEntity.getBody();

        assertNotNull(response);

        // レスポンスボディに変換されるオブジェクトの検査
        assertFalse(response.isOk());
        assertNotNull(response.getDnsResolveResult());
        assertNull(response.getDatabaseName());
        assertNull(response.getDatabaseVersion());
        assertEquals(config, response.getConfig());

        // HTTP の取り扱いの検査
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}
