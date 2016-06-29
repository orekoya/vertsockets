package io.hyperbuffer.samples.vertsockets.client.core.config;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author vorekoya on 29/06/2016.
 */
@Configuration
public class BeanProducer {

    @Bean
    public HttpClientOptions getHttpClientOptions() {
        return new HttpClientOptions()
                .setVerifyHost(true)
                .setKeepAlive(true).setConnectTimeout(3000)
                .setDefaultHost("127.0.0.1").setDefaultPort(48080);
    }

    @Bean
    public Vertx getVertxClient() {
        return Vertx.vertx(new VertxOptions());
    }

    @Bean
    @Scope("prototype")
    public HttpClient getHttpClient(Vertx vertx, HttpClientOptions httpClientOptions) {
        return vertx.createHttpClient(httpClientOptions);
    }
}
