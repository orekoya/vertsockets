package io.hyperbuffer.samples.vertsockets.core.util;

import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.stomp.Destination;
import io.vertx.ext.stomp.StompServer;
import io.vertx.ext.stomp.StompServerHandler;
import io.vertx.ext.stomp.StompServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Date;
import java.util.UUID;

/**
 * @author vorekoya on 11/06/2016.
 */
public class ServerVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(ServerVerticle.class);

    private final String identifier;
    private final int port;
    private final StompServerOptions stompServerOptions;
    private final HttpServerOptions httpServerOptions;

    private HttpServer httpServer;
    private StompServer stompServer;


    public ServerVerticle(ApplicationContext context, int port) {
        super();
        this.port = port;
        this.stompServerOptions = context.getBean(StompServerOptions.class);
        this.httpServerOptions = context.getBean(HttpServerOptions.class);
        this.identifier = UUID.randomUUID().toString();
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);

        startFuture.setHandler((AsyncResultHandler<Void>) startEvent -> {

            if (startEvent.succeeded()) {
                this.stompServer = createStompServer(vertx);
                this.httpServer = vertx.createHttpServer(httpServerOptions.setPort(port))
                        .websocketHandler(serverWebSocket -> {
                            LOG.info("client connected");
                            vertx.setPeriodic(3000, event -> {
                                LOG.info("server sending message via ws");
                                serverWebSocket.writeBinaryMessage(Buffer.buffer("Hello World: {}", new Date().toString()));
                            });
                            serverWebSocket.handler(event -> LOG.info("incoming data: {}", event.toString()));
                        })
                        .requestHandler(buildRouter()::accept).listen((AsyncResultHandler<HttpServer>) event -> {
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

    private Router buildRouter() {
        final Router router = Router.router(vertx);

        //this is just a pingable http endpoint
        router.route(HttpMethod.GET, "/api/ping").handler(event -> {
            String msg = String.format("pinged api with content-type %s at %s ", event.getAcceptableContentType(), new Date());
            LOG.info(msg);
            event.response().end(msg);
        });

        //this is a websocket endpoint
        router.route(HttpMethod.POST, "/stomp").handler(event -> {
            final String msg = String.format("called stomp endpoint with body %s at %s ", event.getBodyAsString(), new Date());
            LOG.info(msg);
            event.response().write(msg);
            event.request().upgrade();
            LOG.info("request upgraded now");
        });

        router.route().failureHandler(new Handler<RoutingContext>() {
            @Override
            public void handle(RoutingContext event) {
                System.out.println(event.request().toString());
            }
        });

        return router;
    }

    private StompServer createStompServer(Vertx vertx) {

        return StompServer.create(vertx, stompServerOptions)
                .handler(StompServerHandler.create(vertx)
                        .destinationFactory((vtx, name) -> {
                            if (name.startsWith("/forbidden")) {
                                LOG.info("stomp server path forbidden");
                                return null;
                            } else if (name.startsWith("/queue")) {
                                LOG.info("stomp server path queue: {}", name);
                                return Destination.queue(vtx, name);
                            } else {
                                LOG.info("stomp server path topic: {}", name);
                                return Destination.topic(vtx, name);
                            }
                        }));

    }


}
