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

package io.nity.grpc.sample.grpc;

import io.grpc.ServerBuilder;
import io.nity.grpc.server.GrpcServerBuilderConfigurer;
import io.nity.grpc.server.autoconfigure.GrpcServerProperties;
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
     * ServerBuilder配置器，在serverBuilder.build().start()前调用，适用所有模式
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
