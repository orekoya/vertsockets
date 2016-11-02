package io.hyperbuffer.samples.vertsockets.client.core.util;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.UUID;

/**
 * @author vorekoya on 29/06/2016.
 */
@Component("normalClient")
@Scope("prototype")
public class NormalHyperClient {
    private final Logger LOG = LoggerFactory.getLogger(NormalHyperClient.class);
    private final String identifier;
    private final HttpClient httpClient;

    @Autowired
    public NormalHyperClient(Vertx vertx, @Qualifier("normalHttpOptions") HttpClientOptions httpClientOptions) {
        this.httpClient = vertx.createHttpClient(httpClientOptions);
        this.identifier = UUID.randomUUID().toString();
        LOG.info("starting hyperclient with client id => {}", identifier);
    }

    String getIdentifier() {
        return identifier;
    }

    HttpClient getHttpClient() {
        return httpClient;
    }

    @PreDestroy
    public void close() {
        LOG.info("shutting down normal hyperclient");
        httpClient.close();
    }

}
