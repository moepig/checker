package com.github.moepig.checker.json;

import com.github.moepig.checker.config.AmazonSQSConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SqsException;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

@RestController
@RequestMapping("/json/amazonsqs")
public class AmazonSQSController {
    private static final Logger _logger = LoggerFactory.getLogger(AmazonSQSController.class);

    private final AmazonSQSConfig _config;

    @Autowired
    public AmazonSQSController(AmazonSQSConfig config) {
        _config = config;
    }

    @GetMapping("")
    public ResponseEntity<AmazonSQSResponse> check() {
        var responseData = new AmazonSQSResponse();

        responseData.setConfig(_config);

        var region = Region.of(_config.getRegion());
        var clientBuilder = SqsClient.builder()
                .region(region); // エンドポイント指定は場合によるので、まだ build() しない

        URI endpointOverrideUri = null;
        if (_config.getEndpointUri() != null && !_config.getEndpointUri().equals("")) {
            try {
                endpointOverrideUri = new URI(_config.getEndpointUri());
            } catch (URISyntaxException e) {
                _logger.error("URI error, input string: " + _config.getEndpointUri(), e);
                responseData.setOk(false);

                return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            clientBuilder = clientBuilder
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create("foo", "foo")
                    ))
                    .endpointOverride(endpointOverrideUri);
        }

        var client = clientBuilder.build();

        // 名前解決のチェック
        try {
            String host;
            if (endpointOverrideUri != null) {
                host = endpointOverrideUri.getHost();
            } else {
                host = "sqs" + region + ".amazonaws.com";
            }
            var address = InetAddress.getByName(host);

            responseData.setDnsResolveResult(address.getHostAddress());
        } catch (UnknownHostException e) {
            _logger.error("dns resolve error", e);
            responseData.setOk(false);

            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 実プロトコルでの接続チェック
        try {
            var listQueues = client.listQueues();

            responseData.setRequestId(listQueues.responseMetadata().requestId());
        } catch (SqsException e) {
            _logger.error(e.awsErrorDetails().errorMessage(), e);
            responseData.setOk(false);

            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (SdkClientException e) {
            _logger.error("sdk client error", e);
            responseData.setOk(false);

            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 早期リターンしてなかったら成功扱い
        responseData.setOk(true);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}
