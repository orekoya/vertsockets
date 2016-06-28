package io.hyperbuffer.samples.vertsockets.core.util;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResultHandler;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.stomp.*;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Date;
import java.util.UUID;

/**
 * @author vorekoya on 11/06/2016.
 */
public class ServerVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(ServerVerticle.class);
    private HttpServer httpServer;
    private final String identifier;
    private final ApplicationContext applicationContext;
    private final int port;
    private StompServer stompServer;

    public ServerVerticle(ApplicationContext context, int port) {
        super();
        this.applicationContext = context;
        this.port = port;
        this.identifier = UUID.randomUUID().toString();
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);
        final Router router = Router.router(vertx);
        router.route(HttpMethod.GET, "/api/ping").handler(event -> {
            System.out.println(String.format("pinged %s at %s ", event.toString(), new Date()));
        });
        router.route(HttpMethod.POST, "/api/connect/").handler(event -> {
            System.out.println(String.format("connect %s at %s ", event.toString(), new Date()));
        });

        router.route().handler(routingContext -> {
            LOG.info("received event at {}: ", new Date());
            routingContext.response().end("hello there: " + identifier);
        });

        this.stompServer = createStompServer(vertx);

        startFuture.setHandler((AsyncResultHandler<Void>) startEvent -> {
            this.httpServer = vertx.createHttpServer(applicationContext.getBean(HttpServerOptions.class).setPort(port));

            if (startEvent.succeeded()) {
                httpServer.websocketHandler(webSocketEvent -> {
                    if (webSocketEvent.headers().isEmpty()) {
                        LOG.info("received web-scoket event at {}: ", new Date());

                    }
                });

                httpServer.requestHandler(router::accept).listen((AsyncResultHandler<HttpServer>) event -> {
                    if (event.succeeded()) {
                        LOG.info("server verticle with UUID : {} has started http server. details: {}", identifier, event.toString());
                    } else {
                        LOG.error("server verticle with UUID : {} failed to start the http server. details: {}", identifier, event.toString());
                    }
                });

            }
        });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        super.stop(stopFuture);

        stopFuture.setHandler((AsyncResultHandler<Void>) stopEvent -> httpServer.close((AsyncResultHandler<Void>) event -> {
            if (event.succeeded()) {
                LOG.info("server verticle has stopped http server. details: " + event.toString());
                stopFuture.complete();
            } else {
                final String message = "server verticle failed to stop the http server. details: " + event.toString();
                LOG.error(message);
                stopFuture.fail(message);
            }
        }));

    }

    private StompServer createStompServer(Vertx vertx) {

        return StompServer.create(vertx)
                .handler(StompServerHandler.create(vertx).destinationFactory((vtx, name) -> {
                    if (name.startsWith("/forbidden")) {
                        return null;
                    } else if (name.startsWith("/queue")) {
                        return Destination.queue(vtx, name);
                    } else {
                        return Destination.topic(vtx, name);
                    }
                }).beginHandler(new DefaultBeginHandler()).closeHandler(StompServerConnection::close))
                .listen(-1, "0.0.0.0");
    }

}
