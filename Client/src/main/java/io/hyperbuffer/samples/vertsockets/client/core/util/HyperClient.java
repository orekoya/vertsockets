package io.hyperbuffer.samples.vertsockets.client.core.util;

import io.vertx.core.http.HttpClient;

import java.util.UUID;

/**
 * @author vorekoya on 29/06/2016.
 */
public class HyperClient {
    private final String identifier;
    private final HttpClient httpClient;

    public HyperClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.identifier = UUID.randomUUID().toString();
    }

    public String getIdentifier() {
        return identifier;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }
}
