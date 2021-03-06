package io.hyperbuffer.samples.vertsockets.core;

import io.hyperbuffer.samples.vertsockets.core.util.ServerVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;


/**
 * @author vorekoya on 18/06/2016.
 */
@Component
public class Initializer implements ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(Initializer.class);
    private ApplicationContext applicationContext;
    private final VertxOptions originalVertxOptions;
    private final VertxOptions alternateVertxOptions;
    private final int port1;
    private final int port2;
    private static int index = 0;

    @Autowired
    public Initializer(
            @Qualifier("originalOptions") VertxOptions originalVertxOptions,
            @Qualifier("alternateOptions") VertxOptions alternateVertxOptions,
            @Value("${http.port.1}") int port1,
            @Value("${http.port.2}") int port2) throws Exception {
        this.originalVertxOptions = originalVertxOptions;
        this.alternateVertxOptions = alternateVertxOptions;
        this.port1 = port1;
        this.port2 = port2;
    }

    @PostConstruct
    public void setup() {
        createVertxInstance(originalVertxOptions, port1);
//        createVertxInstance(alternateVertxOptions, port2);
    }


    private void createVertxInstance(VertxOptions vertxOptions, int port) {

        LOG.info("Deploying vertx instance {}: {}", index, new Date());
        Vertx.clusteredVertx(vertxOptions, result -> {
            if (result.succeeded()) {
                Vertx vertx = result.result();
                index++;

                LOG.info("vertx {} initialized", index);
                deploy(vertx, this.applicationContext, port, 1);

            } else {
                LOG.error("vertx {} setup failed: {}", index, result.cause());
            }
        });


    }

    private static void deploy(Vertx vertx, ApplicationContext applicationContext, int port, int verticleCount) {

        for (int count = 0; count < verticleCount; count++) {

            final ServerVerticle serverVerticle = new ServerVerticle(applicationContext, port);

            vertx.deployVerticle(serverVerticle, result -> {
                if (result.succeeded()) {
                    LOG.info("http server verticle deployed at : {}", new Date());
                } else {
                    LOG.info("http server verticle failed to deploy at : {}", new Date());
                    if (result.cause() != null) result.cause().printStackTrace();
                }
            });
        }

    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
