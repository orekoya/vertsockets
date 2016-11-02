package io.hyperbuffer.samples.vertsockets.client.core.config;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpClientOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author vorekoya on 29/06/2016.
 */
@Configuration
public class BeanProducer {

    @Autowired
    @Bean(name = "wsHttpOptions")
    public HttpClientOptions getWSClientOptions(@Value("${websocket.server.port}") int port, @Value("${websocket.server.address}") String host) {
        return new HttpClientOptions()
                .setVerifyHost(true)
                .setKeepAlive(true)
                .setConnectTimeout(3000)
                .setDefaultHost(host).setDefaultPort(port);
    }

    @Autowired
    @Bean(name = "normalHttpOptions")
    public HttpClientOptions getHttpClientOptions(@Value("${http.server.port}") int port, @Value("${http.server.address}") String host) {
        return new HttpClientOptions()
                .setVerifyHost(true)
                .setKeepAlive(true)
                .setConnectTimeout(3000)
                .setDefaultHost(host).setDefaultPort(port);
    }

    @Bean(destroyMethod = "close")
    public Vertx getVertxClient() {
        return Vertx.vertx(new VertxOptions().setClustered(false));
    }

}
