package io.nity.grpc.autoconfigure;

import io.grpc.ServerBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.nity.grpc.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

@ConditionalOnBean(annotation = GrpcService.class)
@EnableConfigurationProperties(GrpcServerProperties.class)
public class GrpcServerBuilderInProcessConfiguration {

    private static final Logger log = LoggerFactory.getLogger(GrpcServerBuilderInProcessConfiguration.class);

    @Autowired
    private GrpcServerProperties grpcProperties;

    @Bean
    @ConditionalOnProperty(value = "grpc.server.model", havingValue = GrpcServerProperties.SERVER_MODEL_IN_PROCESS)
    public ServerBuilder getServerBuilder() {
        GrpcServerProperties.ServerProperties server = grpcProperties.getServer();
        ServerBuilder serverBuilder;

        String inProcessServerName = server.getInProcessServerName();

        if (!StringUtils.hasText(inProcessServerName)) {
            log.error("please config required property [inProcessServerName] for InProcess model");
            throw new RuntimeException("Failed to create inProcessServer");
        }

        log.warn("gRPC Server will run in InProcess model. Please only use in testing");
        serverBuilder = InProcessServerBuilder.forName(inProcessServerName);

        return serverBuilder;
    }

}
