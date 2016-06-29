package io.hyperbuffer.samples.vertsockets.client.core.util;

import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.impl.HttpClientImpl;
import io.vertx.core.impl.VertxInternal;

import java.util.UUID;

/**
 * @author vorekoya on 29/06/2016.
 */
public class HyperClient extends HttpClientImpl {
    private final String identifier;

    public HyperClient(VertxInternal vertx, HttpClientOptions options) {
        super(vertx, options);
        this.identifier = UUID.randomUUID().toString();

    }

    public String getIdentifier() {
        return identifier;
    }
}
