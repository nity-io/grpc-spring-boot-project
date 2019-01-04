package io.nity.grpc.sample.grpc;

import io.grpc.Channel;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.nity.grpc.autoconfigure.GrpcClientProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * grpc存根配置类，完成所有Stub的初始化
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(GrpcClientProperties.class)
public class GrpcStubConfig {

    @Autowired
    protected GrpcClientProperties grpcProperties;

    @Bean
    public GreeterGrpc.GreeterBlockingStub getGreeterBlockingStub(Channel channel) {
        GreeterGrpc.GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(channel);
        return blockingStub;
    }

}
