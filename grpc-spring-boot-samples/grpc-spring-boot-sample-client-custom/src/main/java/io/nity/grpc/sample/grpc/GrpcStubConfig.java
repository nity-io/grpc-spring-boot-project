package io.nity.grpc.sample.grpc;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.manualflowcontrol.StreamingGreeterGrpc;
import io.nity.grpc.DisposableManagedChannel;
import io.nity.grpc.autoconfigure.GrpcClientProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * grpc存根配置类，完成自定义Channel和所有Stub的初始化
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(GrpcClientProperties.class)
public class GrpcStubConfig {

    @Autowired
    protected GrpcClientProperties grpcClientProperties;

    @Bean
    public GreeterGrpc.GreeterBlockingStub getGreeterBlockingStub(Channel channel) {
        GreeterGrpc.GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(channel);
        return blockingStub;
    }

    @Bean
    public StreamingGreeterGrpc.StreamingGreeterStub getStreamingGreeterStub(Channel channel) {
        StreamingGreeterGrpc.StreamingGreeterStub streamingGreeterStub = StreamingGreeterGrpc.newStub(channel);
        return streamingGreeterStub;
    }

    @Bean
    @ConditionalOnProperty(value = "grpc.stub.model", havingValue = GrpcClientProperties.SERVER_MODEL_CUSTOM)
    public DisposableManagedChannel getChannel() {
        GrpcClientProperties.StubProperties stub = grpcClientProperties.getStub();
        int port = stub.getPort();
        ManagedChannel channel;
        String host = stub.getHost();

        log.info("will create custom channel");
        log.info("creating channel on {}:{}", host, port);

        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        DisposableManagedChannel disposableManagedChannel = new DisposableManagedChannel(channel);
        return disposableManagedChannel;
    }

}
