package com.acs.brunsi.core.http;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

public final class BasicAuthInterceptor implements HttpRequestInterceptor {

    private final String authorizationValue;

    public BasicAuthInterceptor(String username, String password) {
        String encoded = Base64.getEncoder().encodeToString(
            (username + ":" + password).getBytes(StandardCharsets.UTF_8)
        );
        this.authorizationValue = "Basic " + encoded;
    }

    @Override
    public void process(HttpRequest request, HttpContext context) {
        if (!request.containsHeader(HttpHeaders.AUTHORIZATION)) {
            request.setHeader(HttpHeaders.AUTHORIZATION, authorizationValue);
        }
    }
}
