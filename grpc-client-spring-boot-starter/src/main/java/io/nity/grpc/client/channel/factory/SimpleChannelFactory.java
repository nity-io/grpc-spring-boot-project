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

package io.nity.grpc.client.channel.factory;

import io.grpc.netty.NettyChannelBuilder;
import io.nity.grpc.client.channel.configurer.GrpcChannelBuilderConfigurer;
import io.nity.grpc.client.channel.configurer.GrpcChannelConfigurer;
import io.nity.grpc.client.config.GrpcClientProperties;
import io.nity.grpc.client.config.GrpcClientPropertiesMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class SimpleChannelFactory extends AbstractChannelFactory<NettyChannelBuilder> {

    public SimpleChannelFactory(final GrpcClientPropertiesMap clientPropertiesMap,
                                final GrpcChannelBuilderConfigurer channelBuilderConfigurer,
                                final GrpcChannelConfigurer channelConfigurer) {
        super(clientPropertiesMap, channelBuilderConfigurer, channelConfigurer);
    }

    @Override
    protected NettyChannelBuilder newChannelBuilder(final String name, final GrpcClientProperties clientProperties) {
        String host = clientProperties.getHost();
        int port = clientProperties.getPort();

        if (!StringUtils.hasText(host)) {
            log.error("please config required property [host] for simple model");
            throw new RuntimeException("Failed to create channel without tls");
        }

        log.info("will create channel without tls. recommend only use in internal service");
        log.info("creating channel on {}:{}", host, port);

        NettyChannelBuilder nettyChannelBuilder = NettyChannelBuilder.forAddress(host, port)
                .usePlaintext();

        return nettyChannelBuilder;
    }

}
