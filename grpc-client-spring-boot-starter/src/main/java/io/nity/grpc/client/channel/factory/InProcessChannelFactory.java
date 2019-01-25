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

import io.grpc.inprocess.InProcessChannelBuilder;
import io.nity.grpc.client.channel.configurer.GrpcChannelBuilderConfigurer;
import io.nity.grpc.client.channel.configurer.GrpcChannelConfigurer;
import io.nity.grpc.client.config.GrpcClientProperties;
import io.nity.grpc.client.config.GrpcClientPropertiesMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class InProcessChannelFactory extends AbstractChannelFactory<InProcessChannelBuilder> {

    public InProcessChannelFactory(final GrpcClientPropertiesMap clientPropertiesMap,
                                   final GrpcChannelBuilderConfigurer channelBuilderConfigurer,
                                   final GrpcChannelConfigurer channelConfigurer) {
        super(clientPropertiesMap, channelBuilderConfigurer, channelConfigurer);
    }

    @Override
    protected InProcessChannelBuilder newChannelBuilder(final String name, final GrpcClientProperties clientProperties) {
        log.debug("Creating new channel: {}", clientProperties);
        String inProcessServerName = clientProperties.getInProcessServerName();

        if (!StringUtils.hasText(inProcessServerName)) {
            log.error("please config required property [inProcessServerName] for InProcess model");
            throw new RuntimeException("Failed to create inProcessChannel");
        }

        log.warn("will create InProcessChannel. Please only use in testing");
        return InProcessChannelBuilder.forName(inProcessServerName);
    }

}
