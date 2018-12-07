package io.nity.grpc.autoconfigure;

import io.grpc.ServerBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.services.HealthStatusManager;
import io.nity.grpc.GrpcServerBuilderConfigurer;
import io.nity.grpc.GrpcServerRunner;
import io.nity.grpc.GrpcService;
import io.nity.grpc.context.LocalRunningGrpcPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

@AutoConfigureOrder
@ConditionalOnBean(annotation = GrpcService.class)
@EnableConfigurationProperties(GrpcProperties.class)
public class GrpcAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(GrpcAutoConfiguration.class);

    @LocalRunningGrpcPort
    private int port;

    @Autowired
    private GrpcProperties grpcProperties;

    @Bean
    @ConditionalOnProperty(value = "grpc.server.enabled", havingValue = "true", matchIfMissing = true)
    public GrpcServerRunner grpcServerRunner(GrpcServerBuilderConfigurer configurer) {
        String inProcessServerName = grpcProperties.getServer().getInProcessServerName();

        if (StringUtils.isEmpty(inProcessServerName)) {
            log.info("gRPC Server will listen on port {}.", port);
            return new GrpcServerRunner(configurer, ServerBuilder.forPort(port));
        } else {
            log.info("gRPC Server will run in InProcess model.");
            return new GrpcServerRunner(configurer, InProcessServerBuilder.forName(inProcessServerName));
        }
    }

    @Bean
    public HealthStatusManager healthStatusManager() {
        return new HealthStatusManager();
    }

    @Bean
    @ConditionalOnMissingBean(GrpcServerBuilderConfigurer.class)
    public GrpcServerBuilderConfigurer serverBuilderConfigurer() {
        return new GrpcServerBuilderConfigurer();
    }
}
