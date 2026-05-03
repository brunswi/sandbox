package com.acs.brunsi.core.http;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.kttn.aem.http.HttpClientProvider;
import org.kttn.aem.http.auth.HttpClientCustomizer;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(service = AioRuntimeService.class)
@Designate(ocd = AioRuntimeService.Config.class)
public class AioRuntimeService {

    private static final String CLIENT_KEY = "aio-runtime-sandbox";

    @ObjectClassDefinition(name = "Sandbox - AIO Runtime Service")
    @interface Config {
        @AttributeDefinition(name = "Action URL")
        String actionUrl() default "https://<namespace>.adobeioruntime.net/api/v1/web/<package>/<action>";
    }

    @Reference
    private HttpClientProvider httpClientProvider;

    @Reference(target = "(service.pid=org.kttn.aem.http.auth.adobe.impl.AdobeIntegrationConfiguration~aio-runtime-sandbox)")
    private HttpClientCustomizer adobeCustomizer;

    private CloseableHttpClient http;
    private String actionUrl;

    @Activate
    void activate(Config config) {
        actionUrl = config.actionUrl();
        http = httpClientProvider.provide(CLIENT_KEY, adobeCustomizer::customize);
    }

    public String invokeAction() throws IOException {
        log.debug("Invoking AIO action: {}", actionUrl);
        try (CloseableHttpResponse response = http.execute(new HttpGet(actionUrl))) {
            log.info("AIO action {} -> {}", actionUrl, response.getStatusLine());
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        }
    }
}
