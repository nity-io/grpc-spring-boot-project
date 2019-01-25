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

package io.nity.grpc.client.autoconfigure;

import io.grpc.ManagedChannelBuilder;
import io.nity.grpc.client.channel.configurer.GrpcChannelBuilderConfigurer;
import io.nity.grpc.client.channel.configurer.GrpcChannelConfigurer;
import io.nity.grpc.client.channel.factory.CustomChannelFactory;
import io.nity.grpc.client.channel.factory.GrpcChannelFactoryFacede;
import io.nity.grpc.client.config.GrpcClientProperties;
import io.nity.grpc.client.config.GrpcClientPropertiesMap;
import io.nity.grpc.client.inject.GrpcClientBeanPostProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@AutoConfigureOrder
public class GrpcClientAutoConfiguration {

    @Bean
    public GrpcClientBeanPostProcessor grpcClientBeanPostProcessor(final ApplicationContext applicationContext) {
        return new GrpcClientBeanPostProcessor(applicationContext);
    }

    @ConditionalOnMissingBean
    @Bean
    public GrpcClientPropertiesMap clientPropertiesMap() {
        return new GrpcClientPropertiesMap();
    }

    @ConditionalOnMissingBean(GrpcChannelBuilderConfigurer.class)
    @Bean
    public GrpcChannelBuilderConfigurer defaultChannelBuilderConfigure() {
        return (channelBuilder, name) -> {
            log.info("configure in defaultChannelBuilderConfigure, no op...");
            //no op
        };
    }

    @ConditionalOnMissingBean(GrpcChannelConfigurer.class)
    @Bean
    public GrpcChannelConfigurer defaultChannelConfigurer() {
        return (channel, name) -> {
            log.info("configure in defaultChannelConfigurer, no op...");
            //no op
        };
    }

    @Bean
    public GrpcChannelFactoryFacede grpcChannelFactoryFacede(final ApplicationContext applicationContext,
                                                             final GrpcClientPropertiesMap clientPropertiesMap,
                                                             final GrpcChannelBuilderConfigurer channelBuilderConfigurer,
                                                             final GrpcChannelConfigurer channelConfigurer) {
        return new GrpcChannelFactoryFacede(applicationContext, clientPropertiesMap, channelBuilderConfigurer, channelConfigurer);
    }

    @ConditionalOnMissingBean(CustomChannelFactory.class)
    @Bean
    public CustomChannelFactory customChannelFactory(final GrpcClientPropertiesMap clientPropertiesMap,
                                                     final GrpcChannelBuilderConfigurer channelBuilderConfigurer,
                                                     final GrpcChannelConfigurer channelConfigurer) {
        return new CustomChannelFactory(clientPropertiesMap, channelBuilderConfigurer, channelConfigurer) {
            @Override
            protected ManagedChannelBuilder newChannelBuilder(final String name, final GrpcClientProperties clientProperties) {
                throw new RuntimeException("must create CustomChannelFactory instance for custom model!");
            }
        };
    }

}
