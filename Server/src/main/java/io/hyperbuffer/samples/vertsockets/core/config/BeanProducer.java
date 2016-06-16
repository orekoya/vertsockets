package io.hyperbuffer.samples.vertsockets.core.config;

import io.hyperbuffer.samples.vertsockets.core.vert.Instance;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author vorekoya on 10/06/2016.
 */
@Configuration
public class BeanProducer {


    @Bean
    public VertxOptions getVertxOptions() {
        return new VertxOptions().setWorkerPoolSize(10).setHAEnabled(true);
    }

    @Bean
    public HttpServerOptions getHttpServerOptions() {
        return new HttpServerOptions()
                .setMaxWebsocketFrameSize(1000000)
                .setCompressionSupported(true)
                .setPort(48080)
                .setHost("0.0.0.0")
                .setClientAuth(ClientAuth.REQUEST);
    }

    @Bean
    @Autowired
    public HttpServer getHttpServer(Instance vertxInstance, HttpServerOptions httpServerOptions) {
        return vertxInstance.getVertxInstance().createHttpServer(httpServerOptions);
    }
}
