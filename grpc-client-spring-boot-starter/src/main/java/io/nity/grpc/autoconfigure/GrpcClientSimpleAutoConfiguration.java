package io.nity.grpc.autoconfigure;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.nity.grpc.DisposableManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

@AutoConfigureOrder
public class GrpcClientSimpleAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(GrpcClientSimpleAutoConfiguration.class);

    @Autowired
    private GrpcClientProperties grpcProperties;

    @Bean
    @ConditionalOnProperty(value = "grpc.stub.model", havingValue = GrpcClientProperties.SERVER_MODEL_SIMPLE)
    public DisposableManagedChannel getChannel() {
        GrpcClientProperties.StubProperties stub = grpcProperties.getStub();
        int port = stub.getPort();
        ManagedChannel channel;
        String host = stub.getHost();

        if (!StringUtils.hasText(host)) {
            log.error("please config required property [host] for simple model");
            throw new RuntimeException("Failed to create channel without tls");
        }

        log.info("will create channel without tls. recommend only use in internal service");
        log.info("creating channel on {}:{}", host, port);

        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        DisposableManagedChannel disposableManagedChannel = new DisposableManagedChannel(channel);
        return disposableManagedChannel;
    }

}
