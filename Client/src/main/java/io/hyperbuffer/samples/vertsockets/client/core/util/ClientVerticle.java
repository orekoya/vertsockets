package io.hyperbuffer.samples.vertsockets.client.core.util;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResultHandler;
import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Date;
import java.util.UUID;

/**
 * @author vorekoya on 11/06/2016.
 */
public class ClientVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(ClientVerticle.class);

    private final String identifier;
    private final ApplicationContext applicationContext;


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
                LOG.info("Client verticle {} has started with 1 http client", identifier);
                final HyperClient pingHyperClient = applicationContext.getBean("original", HyperClient.class);
                final HyperClient connectHyperClient = applicationContext.getBean("alternate", HyperClient.class);

                Long pingTimerID = vertx.setPeriodic(10000, event -> {
                    makePingRequest(pingHyperClient);
                });
                LOG.info("started ping timer with ID: {}", pingTimerID);

                makeConnectRequest(connectHyperClient);
                LOG.info("started web-socket connection at {}", new Date());

            }
        });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        super.stop(stopFuture);
        stopFuture.setHandler(event -> LOG.info("verticle stopped at : {}", new Date()));
    }


    private void makePingRequest(HyperClient hyperClient) {
        hyperClient.getHttpClient().getNow("/api/ping", event ->
                LOG.info("ping client [{}] response code : {}, message: {}, headers: {}", hyperClient.getIdentifier()
                        , event.statusCode()
                        , event.statusMessage()
                        , event.headers().size()
                ));
    }

    private void makeConnectRequest(HyperClient hyperClient) {
        hyperClient.getHttpClient().post("/api/connect", event ->
                LOG.info("connect client [{}] response code : {}, message: {}, headers: {}", hyperClient.getIdentifier()
                        , event.statusCode()
                        , event.statusMessage()
                        , event.headers().size()
                ));
    }

}
