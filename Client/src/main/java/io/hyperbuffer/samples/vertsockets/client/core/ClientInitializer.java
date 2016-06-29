package io.hyperbuffer.samples.vertsockets.client.core;

import io.hyperbuffer.samples.vertsockets.client.core.util.ClientVerticle;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;


/**
 * @author vorekoya on 18/06/2016.
 */
@Component
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
        deploy(vertx, applicationContext, 2);
    }

    private static void deploy(Vertx vertx, ApplicationContext applicationContext, int verticleCount) {

        for (int count = 0; count < verticleCount; count++) {

            final ClientVerticle ClientVerticle = new ClientVerticle(applicationContext);

            vertx.deployVerticle(ClientVerticle, result -> {
                if (result.succeeded()) {
                    LOG.info("http client verticle deployed at : {}", new Date());
                } else {
                    LOG.info("http client verticle failed to deploy at : {}", new Date());
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
