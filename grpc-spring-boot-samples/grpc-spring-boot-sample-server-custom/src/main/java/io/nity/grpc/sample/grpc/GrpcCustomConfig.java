package io.nity.grpc.sample.grpc;

import io.grpc.ServerBuilder;
import io.nity.grpc.GrpcServerBuilderConfigurer;
import io.nity.grpc.autoconfigure.GrpcServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * grpc自定义配置类
 */
@Configuration
@EnableConfigurationProperties(GrpcServerProperties.class)
public class GrpcCustomConfig {

    private static final Logger log = LoggerFactory.getLogger(GrpcCustomConfig.class);

    @Autowired
    protected GrpcServerProperties grpcServerProperties;

    /**
     * custom模式下ServerBuilder自定义
     */
    @Bean
    @ConditionalOnProperty(value = "grpc.server.model", havingValue = GrpcServerProperties.SERVER_MODEL_CUSTOM)
    public ServerBuilder getServerBuilder() {
        int port = grpcServerProperties.getServer().getPort();
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
