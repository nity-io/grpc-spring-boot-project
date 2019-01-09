package io.nity.grpc.autoconfigure;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.nity.grpc.DisposableManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

@Slf4j
@AutoConfigureOrder
public class GrpcClientSimpleAutoConfiguration {

    @Autowired
    private GrpcClientProperties clientProperties;

    @Bean
    @ConditionalOnProperty(value = "grpc.client.model", havingValue = GrpcClientProperties.SERVER_MODEL_SIMPLE)
    public DisposableManagedChannel getChannel() {
        int port = clientProperties.getPort();
        ManagedChannel channel;
        String host = clientProperties.getHost();

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
