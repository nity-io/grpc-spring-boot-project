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
import io.nity.grpc.server.GrpcService;
import io.nity.grpc.server.context.LocalRunningGrpcPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnBean(annotation = GrpcService.class)
@EnableConfigurationProperties(GrpcServerProperties.class)
public class GrpcServerBuilderSimpleConfiguration {

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
