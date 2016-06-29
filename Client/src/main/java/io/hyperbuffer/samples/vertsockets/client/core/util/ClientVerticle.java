package io.hyperbuffer.samples.vertsockets.client.core.util;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResultHandler;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.UUID;

/**
 * @author vorekoya on 11/06/2016.
 */
public class ClientVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(ClientVerticle.class);

    private final String identifier;
    private final ApplicationContext applicationContext;
    private HttpClient httpClient;


    public ClientVerticle(ApplicationContext context) {
        super();
        this.applicationContext = context;
        this.identifier = UUID.randomUUID().toString();
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);

        startFuture.setHandler((AsyncResultHandler<Void>) startEvent -> {
            if (startEvent.succeeded()) {
                this.httpClient = applicationContext.getBean(HttpClient.class);
                LOG.info("Client verticle {} has started with 1 http client", identifier);
            }
        });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        super.stop(stopFuture);
        stopFuture.setHandler(event -> httpClient.close());
    }


}
