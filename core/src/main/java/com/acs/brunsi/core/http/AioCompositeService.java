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

/**
 * Demonstrates Example 5 (Shared credentials across multiple integrations) from
 * aem-http-foundation EXAMPLES.md.
 *
 * Both clients share one OAuthClientCredentialsTokenSupplier (aio-shared-sandbox) but remain
 * separate integration contexts with independent pooled clients and separate customizer references.
 */
@Slf4j
@Component(service = AioCompositeService.class)
@Designate(ocd = AioCompositeService.Config.class)
public class AioCompositeService {

    @ObjectClassDefinition(name = "Sandbox - AIO Composite Service")
    @interface Config {
        @AttributeDefinition(name = "Action A URL")
        String actionAUrl() default "https://<namespace>.adobeioruntime.net/api/v1/web/<package>/<action-a>";

        @AttributeDefinition(name = "Action B URL")
        String actionBUrl() default "https://<namespace>.adobeioruntime.net/api/v1/web/<package>/<action-b>";
    }

    @Reference
    private HttpClientProvider httpClientProvider;

    @Reference(target = "(service.pid=org.kttn.aem.http.auth.adobe.impl.AdobeIntegrationConfiguration~aio-action-a-sandbox)")
    private HttpClientCustomizer customizerA;

    @Reference(target = "(service.pid=org.kttn.aem.http.auth.adobe.impl.AdobeIntegrationConfiguration~aio-action-b-sandbox)")
    private HttpClientCustomizer customizerB;

    private CloseableHttpClient httpA;
    private CloseableHttpClient httpB;
    private String actionAUrl;
    private String actionBUrl;

    @Activate
    void activate(Config config) {
        actionAUrl = config.actionAUrl();
        actionBUrl = config.actionBUrl();
        httpA = httpClientProvider.provide("aio-action-a-sandbox", customizerA::customize);
        httpB = httpClientProvider.provide("aio-action-b-sandbox", customizerB::customize);
    }

    public String invokeActionA() throws IOException {
        log.debug("Invoking action A: {}", actionAUrl);
        try (CloseableHttpResponse response = httpA.execute(new HttpGet(actionAUrl))) {
            log.info("Action A {} -> {}", actionAUrl, response.getStatusLine());
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        }
    }

    public String invokeActionB() throws IOException {
        log.debug("Invoking action B: {}", actionBUrl);
        try (CloseableHttpResponse response = httpB.execute(new HttpGet(actionBUrl))) {
            log.info("Action B {} -> {}", actionBUrl, response.getStatusLine());
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        }
    }
}
