package com.acs.brunsi.core.http;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.kttn.aem.http.HttpClientProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code POST} then {@code GET} the same pet against
 * <a href="https://petstore.swagger.io/">petstore.swagger.io</a> v2.
 */
@Slf4j
@Component(service = PetStoreService.class)
public class PetStoreService {

    private static final String API = "https://petstore.swagger.io/v2/pet";
    private static final String CLIENT_KEY = "petstore";
    private static final Gson GSON = new Gson();

    @Reference
    private HttpClientProvider httpClientProvider;
    private CloseableHttpClient http;

    @Activate
    void activate() {
        http = httpClientProvider.provide(CLIENT_KEY);
    }

    /**
     * Create a pet at the store, then load that pet by id.
     */
    public Pet roundTrip() throws IOException {
        long id = 150510;
        Pet pet =  Pet.builder()
            .id(id)
            .name("Kamikatzi")
            .status("sold")
            .build();
        HttpPost post = new HttpPost(API);
        post.setEntity(new StringEntity(GSON.toJson(pet), ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse r = http.execute(post)) {
            log.info("POST {} -> {} status={}", post.getURI(), r.getStatusLine());
        }
        try (CloseableHttpResponse r = http.execute(new HttpGet(API + "/" + id))) {
            String text = EntityUtils.toString(r.getEntity(), StandardCharsets.UTF_8);
            return GSON.fromJson(text, Pet.class);
        }
    }

    @Data
    @Builder
    public static class Pet {
        private long id;
        private String name;
        @Builder.Default
        private List<String> photoUrls = new ArrayList<>();
        private String status;
    }
}
