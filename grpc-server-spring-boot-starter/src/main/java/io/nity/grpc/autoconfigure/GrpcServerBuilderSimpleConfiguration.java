package io.nity.grpc.autoconfigure;

import io.grpc.ServerBuilder;
import io.nity.grpc.GrpcService;
import io.nity.grpc.context.LocalRunningGrpcPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@ConditionalOnBean(annotation = GrpcService.class)
@EnableConfigurationProperties(GrpcServerProperties.class)
public class GrpcServerBuilderSimpleConfiguration {

    private static final Logger log = LoggerFactory.getLogger(GrpcServerBuilderSimpleConfiguration.class);

    @LocalRunningGrpcPort
    private int port;

    @Bean
    @ConditionalOnProperty(value = "grpc.server.model", havingValue = GrpcServerProperties.SERVER_MODEL_SIMPLE)
    public ServerBuilder getServerBuilder() {
        ServerBuilder serverBuilder;

        log.info("gRPC Server will run without tls. recommend only use in internal service");
        log.info("gRPC Server will listen on port {}", port);
        serverBuilder = ServerBuilder.forPort(port);

        return serverBuilder;
    }

}