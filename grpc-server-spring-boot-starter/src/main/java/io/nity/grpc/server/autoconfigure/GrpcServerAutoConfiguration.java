/*
 * Copyright 2019 The nity.io gRPC Spring Boot Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nity.grpc.server.autoconfigure;

import io.grpc.ServerBuilder;
import io.grpc.services.HealthStatusManager;
import io.nity.grpc.server.GrpcServerBuilderConfigurer;
import io.nity.grpc.server.GrpcServerRunner;
import io.nity.grpc.server.GrpcService;
import io.nity.grpc.server.context.LocalRunningGrpcPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnBean(annotation = GrpcService.class)
@EnableConfigurationProperties(GrpcServerProperties.class)
public class GrpcServerAutoConfiguration {

    @LocalRunningGrpcPort
    private int port;

    @Autowired
    private GrpcServerProperties serverProperties;

    @Bean
    public GrpcServerRunner grpcServerRunner(ServerBuilder serverBuilder, GrpcServerBuilderConfigurer serverBuilderConfigurer) {
        return new GrpcServerRunner(serverBuilder, serverBuilderConfigurer);
    }

    @Bean
    public HealthStatusManager healthStatusManager() {
        return new HealthStatusManager();
    }

    @Bean
    @ConditionalOnMissingBean(GrpcServerBuilderConfigurer.class)
    public GrpcServerBuilderConfigurer defaultServerBuilderConfigurer() {
        return serverBuilder -> {
            log.info("configure in defaultServerBuilderConfigurer, no op...");
            //no op
        };
    }

}
