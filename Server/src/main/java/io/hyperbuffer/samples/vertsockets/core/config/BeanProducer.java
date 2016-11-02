package io.hyperbuffer.samples.vertsockets.core.config;

import com.hazelcast.config.Config;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.stomp.StompServerOptions;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author vorekoya on 10/06/2016.
 */
@Configuration
public class BeanProducer {

    private static final Logger LOG = LoggerFactory.getLogger(BeanProducer.class);

    @Bean
    public HttpServerOptions getHttpServerOptions1(@Value("${http.port.1}") int httpPort) {
        return new HttpServerOptions()
                .setMaxWebsocketFrameSize(1000000)
                .setCompressionSupported(true)
                .setPort(httpPort)
                .setHost("0.0.0.0");
    }


    @Bean(name = "originalClusterMgr")
    public ClusterManager getClusterManager() {
        final Config hazelcastConfig = new Config();
        hazelcastConfig.getNetworkConfig().getJoin().getTcpIpConfig().addMember("127.0.0.1").setEnabled(true);
        hazelcastConfig.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        hazelcastConfig.setInstanceName("originalInstance");

        return new HazelcastClusterManager(hazelcastConfig);
    }

    @Bean(name = "alternateClusterMgr")
    public ClusterManager getAlternateClusterManager() {
        final Config hazelcastConfig = new Config();
        hazelcastConfig.getNetworkConfig().getJoin().getTcpIpConfig().addMember("127.0.0.1").setEnabled(true);
        hazelcastConfig.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        hazelcastConfig.setInstanceName("alternateInstance");

        return new HazelcastClusterManager(hazelcastConfig);
    }

    @Bean(name = "originalOptions")
    @Autowired
    public VertxOptions getOriginalVertxOptions(@Qualifier("originalClusterMgr") ClusterManager clusterManager) {
        return new VertxOptions()
                .setClusterManager(clusterManager)
                .setClustered(true).setQuorumSize(2).setHAGroup("x-group")
                .setHAEnabled(true);
    }

    @Bean(name = "alternateOptions")
    @Autowired
    public VertxOptions getAlternateVertxOptions(@Qualifier("alternateClusterMgr") ClusterManager clusterManager) {
        return new VertxOptions()
                .setClusterManager(clusterManager)
                .setClustered(true).setQuorumSize(2).setHAGroup("x-group")
                .setHAEnabled(true);
    }

    @Bean
    public StompServerOptions getStompServerOptions() {
        return new StompServerOptions()
                .setPort(-1) //defer port setting until later
                .setHost("127.0.0.1")
                .setWebsocketBridge(true)
                .setWebsocketPath("/stomp");
    }
}
