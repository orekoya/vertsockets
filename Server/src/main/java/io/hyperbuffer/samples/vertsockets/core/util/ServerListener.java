package io.hyperbuffer.samples.vertsockets.core.util;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author vorekoya on 15/06/2016.
 */
@Component
public class ServerListener {

    private final HttpServer httpServer;

    @Autowired
    public ServerListener(HttpServer httpServer) {
        this.httpServer = httpServer;
        this.httpServer.requestHandler(new RequestHandler());
    }

    private class RequestHandler implements Handler<HttpServerRequest> {

        /**
         * Something has happened, so handle it.
         *
         * @param event the event to handle
         */
        @Override
        public void handle(HttpServerRequest event) {
            System.out.println(event.toString());
            System.out.println(event.headers().toString());
            event.response().end("hello there");
        }
    }
}
