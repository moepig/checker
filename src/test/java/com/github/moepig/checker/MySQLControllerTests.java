package com.github.moepig.checker;

import com.github.moepig.checker.config.MySQLConfig;
import com.github.moepig.checker.json.MySQLController;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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
        var response = controller.check();

        assertTrue(response.isOk());
        assertNotNull(response.getDnsResolveResult());
        assertNotNull(response.getDatabaseName());
        assertNotNull(response.getDatabaseVersion());
        assertTrue(response.getConfig().equals(config));
    }

    @Test
    void invalidConfig() {
        var config = new MySQLConfig();
        config.setHost("example");
        config.setPort("3306");
        config.setSchema("nodatabase");
        config.setUsername("nouser");
        config.setPassword("");

        var controller = new MySQLController(config);
        var response = controller.check();

        assertFalse(response.isOk());
        assertNull(response.getDnsResolveResult());
        assertNull(response.getDatabaseName());
        assertNull(response.getDatabaseVersion());
        assertTrue(response.getConfig().equals(config));
    }
}
