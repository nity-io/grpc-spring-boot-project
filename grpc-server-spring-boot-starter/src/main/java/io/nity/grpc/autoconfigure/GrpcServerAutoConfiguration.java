package io.nity.grpc.autoconfigure;

import io.grpc.ServerBuilder;
import io.grpc.services.HealthStatusManager;
import io.nity.grpc.GrpcServerBuilderConfigurer;
import io.nity.grpc.GrpcServerRunner;
import io.nity.grpc.GrpcService;
import io.nity.grpc.context.LocalRunningGrpcPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Slf4j
@ConditionalOnBean(annotation = GrpcService.class)
@EnableConfigurationProperties(GrpcServerProperties.class)
public class GrpcServerAutoConfiguration {

    @LocalRunningGrpcPort
    private int port;

    @Autowired
    private GrpcServerProperties grpcProperties;

    @Bean
    public GrpcServerRunner grpcServerRunner(ServerBuilder serverBuilder, GrpcServerBuilderConfigurer configurer) {
        return new GrpcServerRunner(serverBuilder, configurer);
    }

    @Bean
    public HealthStatusManager healthStatusManager() {
        return new HealthStatusManager();
    }

    @Bean
    @ConditionalOnMissingBean(GrpcServerBuilderConfigurer.class)
    public GrpcServerBuilderConfigurer serverBuilderConfigurer() {
        return new GrpcServerBuilderConfigurer() {
            @Override
            public void configure(ServerBuilder<?> serverBuilder) {

            }
        };
    }

}
