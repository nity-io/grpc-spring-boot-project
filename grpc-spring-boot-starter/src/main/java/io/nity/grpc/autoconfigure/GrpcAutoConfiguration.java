package io.nity.grpc.autoconfigure;

import io.grpc.ServerBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.services.HealthStatusManager;
import io.nity.grpc.GrpcServerRunner;
import io.nity.grpc.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import io.nity.grpc.GrpcServerBuilderConfigurer;
import io.nity.grpc.context.LocalRunningGrpcPort;

@AutoConfigureOrder
@ConditionalOnBean(annotation = GrpcService.class)
@EnableConfigurationProperties(GrpcProperties.class)
public class GrpcAutoConfiguration {

    @LocalRunningGrpcPort
    private int port;

    @Autowired
    private GrpcProperties grpcProperties;

    @Bean
    @ConditionalOnProperty(value = "grpc.server.enabled", havingValue = "true", matchIfMissing = true)
    public GrpcServerRunner grpcServerRunner(GrpcServerBuilderConfigurer configurer) {
        return new GrpcServerRunner(configurer, ServerBuilder.forPort(port));
    }

    @Bean
    @ConditionalOnExpression("#{environment.getProperty('grpc.server.inProcessServerName','')!=''}")
    public GrpcServerRunner grpcInprocessServerRunner(GrpcServerBuilderConfigurer configurer) {
        return new GrpcServerRunner(configurer, InProcessServerBuilder.forName(grpcProperties.getServer().getInProcessServerName()));
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
