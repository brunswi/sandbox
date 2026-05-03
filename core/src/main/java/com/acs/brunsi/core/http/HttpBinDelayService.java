package com.acs.brunsi.core.http;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.kttn.aem.http.HttpClientProvider;
import org.kttn.aem.http.HttpConfigService;
import org.kttn.aem.http.HttpConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import lombok.extern.slf4j.Slf4j;

/**
 * Demonstrates Example 4 (Custom timeouts per integration) from aem-http-foundation EXAMPLES.md.
 * Uses httpbin.org/delay/3 to simulate a slow endpoint that warrants a longer socket timeout.
 */
@Slf4j
@Component(service = HttpBinDelayService.class)
public class HttpBinDelayService {

    private static final String RESOURCE_URL = "https://httpbin.org/delay/3";
    private static final String CLIENT_KEY = "httpbin-delay";

    @Reference
    private HttpClientProvider httpClientProvider;

    @Reference
    private HttpConfigService httpConfigService;

    private CloseableHttpClient http;

    @Activate
    void activate() {
        HttpConfig customConfig = httpConfigService.getHttpConfig().toBuilder()
            .socketTimeout(10_000)
            .connectionTimeout(5_000)
            .build();

        http = httpClientProvider.provide(CLIENT_KEY, customConfig);
    }

    public String fetch() throws IOException {
        log.debug("Calling {}", RESOURCE_URL);
        try (CloseableHttpResponse response = http.execute(new HttpGet(RESOURCE_URL))) {
            log.info("httpbin delay -> {}", response.getStatusLine());
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        }
    }
}
