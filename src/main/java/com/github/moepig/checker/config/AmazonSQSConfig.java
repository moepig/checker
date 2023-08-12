package com.github.moepig.checker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "amazonsqs")
public class AmazonSQSConfig {
    private String region;
    private String endpointUri;
}
