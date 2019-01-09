package io.nity.grpc.sample.grpc;

import io.grpc.ServerBuilder;
import io.nity.grpc.GrpcServerBuilderConfigurer;
import io.nity.grpc.autoconfigure.GrpcServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * grpc自定义配置类
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(GrpcServerProperties.class)
public class GrpcCustomConfig {

    @Autowired
    protected GrpcServerProperties serverProperties;

    /**
     * custom模式下ServerBuilder自定义
     */
    @Bean
    @ConditionalOnProperty(value = "grpc.server.model", havingValue = GrpcServerProperties.SERVER_MODEL_CUSTOM)
    public ServerBuilder getServerBuilder() {
        int port = serverProperties.getPort();
        ServerBuilder<?> serverBuilder = ServerBuilder.forPort(port);
        return serverBuilder;
    }

    /**
     * ServerBuilder配置器，在serverBuilder.build().start()前调用，使用所有模式
     */
    @Bean
    public GrpcServerBuilderConfigurer serverBuilderConfigurer() {
        GrpcServerBuilderConfigurer grpcServerBuilderConfigurer = new GrpcServerBuilderConfigurer() {
            @Override
            public void configure(ServerBuilder<?> serverBuilder) {
                log.info("GrpcServerBuilderConfigurer configure...   serverBuilder:{}", serverBuilder.toString());
            }
        };

        return grpcServerBuilderConfigurer;
    }
}
