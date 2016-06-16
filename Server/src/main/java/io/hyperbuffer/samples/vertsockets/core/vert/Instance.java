package io.hyperbuffer.samples.vertsockets.core.vert;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * @author vorekoya on 10/06/2016.
 */
@Component
public class Instance {

    private final Vertx vertxInstance;

    @Autowired
    public Instance(VertxOptions vertxOptions) {
        vertxInstance = Vertx.vertx(vertxOptions);
    }

    public Vertx getVertxInstance() {
        return vertxInstance;
    }

    @PreDestroy
    public void close() {
        vertxInstance.close();
    }
}
