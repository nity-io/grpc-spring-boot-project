package io.nity.grpc.sample.grpc;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.nity.grpc.autoconfigure.GrpcProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.nity.grpc.CalculatorGrpc;
import io.nity.grpc.GreeterGrpc;

/**
 * grpc存根配置类，完成Channel和所有Stub的初始化
 */
@Configuration
@EnableConfigurationProperties(GrpcProperties.class)
public class GrpcStubConfig {

    @Autowired
    protected GrpcProperties grpcProperties;

    @Bean
    public GreeterGrpc.GreeterBlockingStub getGreeterBlockingStub(Channel channel) {
        GreeterGrpc.GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(channel);
        return blockingStub;
    }

    @Bean
    public CalculatorGrpc.CalculatorBlockingStub getCalculatorBlockingStub(Channel channel) {
        CalculatorGrpc.CalculatorBlockingStub blockingStub = CalculatorGrpc.newBlockingStub(channel);
        return blockingStub;
    }

    @Bean
    @ConditionalOnExpression("#{environment.getProperty('grpc.stub.host','')!=''}")
    public Channel getChannel() {
        String host = grpcProperties.getStub().getHost();
        int port = grpcProperties.getStub().getPort();

        Channel channel = ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build();

        return channel;
    }
}
