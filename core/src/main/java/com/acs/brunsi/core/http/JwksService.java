package com.acs.brunsi.core.http;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.kttn.aem.http.HttpClientProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = JwksService.class)
public class JwksService {

    private static final String JWKS_URL = "https://api.vas-preprod.eu.dp15.vwg-connect.com/jwks";
    private static final String CLIENT_KEY = "vas-jwks";

    @Reference
    private HttpClientProvider httpClientProvider;

    private CloseableHttpClient http;

    @Activate
    void activate() {
        http = httpClientProvider.provide(CLIENT_KEY);
    }

    public String fetchJwks() throws IOException {
        try (CloseableHttpResponse response = http.execute(new HttpGet(JWKS_URL))) {
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        }
    }
}