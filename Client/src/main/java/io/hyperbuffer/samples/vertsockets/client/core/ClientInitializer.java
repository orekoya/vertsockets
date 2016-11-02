package io.hyperbuffer.samples.vertsockets.client.core;

import io.hyperbuffer.samples.vertsockets.client.core.util.ClientVerticle;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * @author vorekoya on 18/06/2016.
 */
@Component
@Lazy(false)
public class ClientInitializer implements ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(ClientInitializer.class);
    private ApplicationContext applicationContext;
    private final Vertx vertx;

    @Autowired
    public ClientInitializer(Vertx vertx) throws Exception {
        this.vertx = vertx;
    }

    @PostConstruct
    public void setup() {
        deploy(vertx, applicationContext, 1);
    }

    /**
     * deploys verticles
     * id multiple verticles are deployed they are used in a round-robin fashion
     * this is mostly inconsequential for a client, though
     *
     * @param vertx              vertx context
     * @param applicationContext spring context
     * @param verticleCount      number of verticles to deploy
     */
    private static void deploy(final Vertx vertx, final ApplicationContext applicationContext, final int verticleCount) {

        for (int count = 0; count < verticleCount; count++) {

            final ClientVerticle ClientVerticle = new ClientVerticle(applicationContext);

            vertx.deployVerticle(ClientVerticle, result -> {
                if (result.succeeded()) {
                    LOG.info("http client verticle deployed ");
                } else {
                    LOG.info("http client verticle failed to deploy. {}", result.result());
                    result.cause().printStackTrace();
                }
            });
        }

    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
