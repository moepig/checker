package com.github.moepig.checker;

import com.github.moepig.checker.config.AmazonSQSConfig;
import com.github.moepig.checker.json.AmazonSQSController;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@Tag("controller")
@SpringBootTest
public class AmazonSQSControllerTests {
    @Test
    void validConfig() {
        var config = new AmazonSQSConfig();
        config.setRegion("us-east-1");
        config.setEndpointUri("http://localhost:9324");

        var controller = new AmazonSQSController(config);
        var responseEntity = controller.check();
        var response = responseEntity.getBody();

        assertNotNull(response);

        // レスポンスボディに変換されるオブジェクトの検査
        assertTrue(response.isOk());
        assertNotNull(response.getDnsResolveResult());
        assertNotNull(response.getRequestId());
        assertNotEquals("", response.getRequestId());
        assertEquals(config, response.getConfig());

        // HTTP の取り扱いの検査
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void invalidConfig_invalidHost() {
        var config = new AmazonSQSConfig();
        config.setRegion("us-east-1");
        config.setEndpointUri("http://example:9324");

        var controller = new AmazonSQSController(config);
        var responseEntity = controller.check();
        var response = responseEntity.getBody();

        assertNotNull(response);

        // レスポンスボディに変換されるオブジェクトの検査
        assertFalse(response.isOk());
        assertNull(response.getDnsResolveResult());
        assertNull(response.getRequestId());
        assertEquals(config, response.getConfig());

        // HTTP の取り扱いの検査
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void invalidConfig_invalidConnection() {
        var config = new AmazonSQSConfig();
        config.setRegion("us-east-1");
        config.setEndpointUri("http://localhost:9320");  // 誤ったポート

        var controller = new AmazonSQSController(config);
        var responseEntity = controller.check();
        var response = responseEntity.getBody();

        assertNotNull(response);

        // レスポンスボディに変換されるオブジェクトの検査
        assertFalse(response.isOk());
        assertNotNull(response.getDnsResolveResult());
        assertNull(response.getRequestId());
        assertEquals(config, response.getConfig());

        // HTTP の取り扱いの検査
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}
