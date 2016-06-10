package io.hyperbuffer.samples.vertsockets.core.config;

import io.vertx.core.VertxOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author vorekoya on 10/06/2016.
 */
@Configuration
public class BeanProducer {


    @Bean
    public VertxOptions getVertxOptions() {
        return new VertxOptions().setWorkerPoolSize(10);
    }
}
