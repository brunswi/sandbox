package com.acs.brunsi.core.http;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.kttn.aem.http.HttpClientProvider;
import org.kttn.aem.http.HttpConfigService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import lombok.extern.slf4j.Slf4j;

/**
 * Demonstrates Example 3 (Custom Basic Auth) from aem-http-foundation EXAMPLES.md.
 * Calls httpbin.org/basic-auth/{user}/{pass} — the path itself defines the expected credentials,
 * so any username/password pair works as long as the Authorization header matches.
 */
@Slf4j
@Component(service = HttpBinService.class)
@Designate(ocd = HttpBinService.Config.class)
public class HttpBinService {

    private static final String CLIENT_KEY = "httpbin-basic-auth";

    @ObjectClassDefinition(name = "Sandbox - HttpBin Basic Auth Service")
    @interface Config {
        @AttributeDefinition(name = "Username")
        String username() default "testuser";

        @AttributeDefinition(name = "Password", type = AttributeType.PASSWORD)
        String password() default "testpass";
    }

    @Reference
    private HttpClientProvider httpClientProvider;

    @Reference
    private HttpConfigService httpConfigService;

    private CloseableHttpClient http;
    private String resourceUrl;

    @Activate
    void activate(Config config) {
        String username = config.username();
        String password = config.password();
        resourceUrl = "https://httpbin.org/basic-auth/" + username + "/" + password;

        http = httpClientProvider.provide(
            CLIENT_KEY,
            httpConfigService.getHttpConfig(),
            builder -> builder.addInterceptorLast(new BasicAuthInterceptor(username, password))
        );
    }

    public String fetch() throws IOException {
        log.debug("Calling {}", resourceUrl);
        try (CloseableHttpResponse response = http.execute(new HttpGet(resourceUrl))) {
            log.info("httpbin basic-auth -> {}", response.getStatusLine());
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        }
    }
}