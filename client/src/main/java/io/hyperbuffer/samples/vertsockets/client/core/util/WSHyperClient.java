package io.hyperbuffer.samples.vertsockets.client.core.util;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author vorekoya on 29/06/2016.
 */
@Component("webSocketClient")
@Scope("prototype")
public class WSHyperClient extends NormalHyperClient {
    private static final Logger LOG = LoggerFactory.getLogger(WSHyperClient.class);

    @Autowired
    public WSHyperClient(Vertx vertx, @Qualifier("wsHttpOptions") HttpClientOptions httpClientOptions) {
        super(vertx, httpClientOptions);
        LOG.info("starting WS client id => {}", getIdentifier());
    }


}
