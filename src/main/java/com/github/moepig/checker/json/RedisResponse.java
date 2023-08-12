package com.github.moepig.checker.json;

import com.github.moepig.checker.config.RedisConfig;
import lombok.Data;

@Data
public class RedisResponse {
    boolean ok;
    String dnsResolveResult;
    String info;
    RedisConfig config;
}
