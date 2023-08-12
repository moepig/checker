package com.github.moepig.checker.json;

import java.util.List;

import com.github.moepig.checker.config.AmazonSQSConfig;
import lombok.Data;

@Data
public class AmazonSQSResponse {
    boolean ok;
    String dnsResolveResult;
    String requestId;
    AmazonSQSConfig config;
}
