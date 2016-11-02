package io.hyperbuffer.samples.vertsockets.client.core.util;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResultHandler;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
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
                LOG.info("Client verticle {} has started with 2 http client", identifier);
                final NormalHyperClient pingNormalHyperClient = applicationContext.getBean("normalClient", NormalHyperClient.class);
                final WSHyperClient wsHyperClient = applicationContext.getBean("webSocketClient", WSHyperClient.class);

                //make a ping request every so often
                Long pingTimerID = vertx.setPeriodic(3000, event -> {
                    makePingRequest(pingNormalHyperClient);
                });
                LOG.info("started ping timer with ID: {}", pingTimerID);

                startStompConnection(wsHyperClient);
                LOG.info("started web-socket connection at {}", new Date());

            } else LOG.error("failed to start client verticle. {}", startEvent.cause().getMessage());
        });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        super.stop(stopFuture);
        stopFuture.setHandler(event -> LOG.info("verticle stopped at : {}", new Date()));
    }


    private void makePingRequest(NormalHyperClient normalHyperClient) {

        normalHyperClient.getHttpClient().getNow("/api/ping", event ->
                LOG.info("ping client [{}] response code : {}, message: {}, headers: {}", normalHyperClient.getIdentifier()
                        , event.statusCode()
                        , event.statusMessage()
                        , event.headers().size()
                ));
    }

    private void startStompConnection(WSHyperClient wsHyperClient) {

        //wsHyperClient.getHttpClient().websocket()
        HttpClient localhost = wsHyperClient.getHttpClient().websocket(48080, "localhost", "/stomp", event -> {
            event.handler(event1 -> LOG.info("server response. {}", event1.toString()));
        }, event -> LOG.error("failure occured whilst connecting:  {}", event.getMessage()));


//        Headers headers = Headers.create().add("Authorization", "Bearer websockets");
//        StompClient.create(vertx)
//                .connect(48080, "127.0.0.1", result -> {
//                    if (result.succeeded()) {
//                        LOG.info("stomp connection made successfully at {}", new Date());
//                        StompClientConnection connection = result.result();
//
//                        connection.subscribe("/queue/myqueue", headers, event ->
//                                        LOG.info("connecttion to subscription: {}", event.getBodyAsString())
//                                , event -> LOG.info("connecttion to receipt handler: {}", event.getBodyAsString())
//                        );
//                        connection.send("", Buffer.buffer("hey there"));
//                    } else {
//                        LOG.error("stomp connection failed at {}", new Date());
//                        result.cause().printStackTrace();
//                    }
//                }).receivedFrameHandler(event -> LOG.info("received frame: {}", event.getBodyAsString()))
//                .errorFrameHandler(event -> LOG.info("received frame: {}", event.getBodyAsString()))
//                .writingFrameHandler(event -> LOG.info("received frame: {}", event.getBodyAsString()));


    }

}
