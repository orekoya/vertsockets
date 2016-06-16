package io.hyperbuffer.samples.vertsockets.core.util;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.AsyncResultHandler;
import io.vertx.core.http.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author vorekoya on 11/06/2016.
 */
@Component
public class ServerVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(ServerVerticle.class);
    private final HttpServer httpServer;

    @Autowired
    public ServerVerticle(HttpServer httpServer) {
        this.httpServer = httpServer;
    }

    /**
     * If your verticle does a simple, synchronous start-up then override this method and put your start-up
     * code in there.
     *
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        super.start();
        httpServer.listen((AsyncResultHandler<HttpServer>) event -> {
            if (event.succeeded())
                LOG.info("server verticle has started http server. details: " + event.toString());
            else
                LOG.error("server verticle failed to start the http server. details: " + event.toString());
        });
    }

    /**
     * If your verticle has simple synchronous clean-up tasks to complete then override this method and put your clean-up
     * code in there.
     *
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        super.stop();
        httpServer.close((AsyncResultHandler<Void>) event -> {
            if (event.succeeded())
                LOG.info("server verticle has stopped http server. details: " + event.toString());
            else
                LOG.error("server verticle failed to stop the http server. details: " + event.toString());

        });
    }
}
