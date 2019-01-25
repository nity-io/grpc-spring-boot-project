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

import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.nity.grpc.client.channel.DisposableManagedChannel;
import io.nity.grpc.client.channel.configurer.GrpcChannelBuilderConfigurer;
import io.nity.grpc.client.channel.configurer.GrpcChannelConfigurer;
import io.nity.grpc.client.config.GrpcClientProperties;
import io.nity.grpc.client.config.GrpcClientPropertiesMap;
import org.springframework.context.ApplicationContext;

import java.util.List;

public class GrpcChannelFactoryFacede implements GrpcChannelFactory {

    private GrpcClientPropertiesMap clientPropertiesMap;

    private final GrpcChannelFactory inProcessChannelFactory;
    private final GrpcChannelFactory simpleChannelFactory;
    private final GrpcChannelFactory tlsChannelFactory;
    private final GrpcChannelFactory customChannelFactory;

    public GrpcChannelFactoryFacede(final ApplicationContext applicationContext,
                                    final GrpcClientPropertiesMap clientPropertiesMap,
                                    final GrpcChannelBuilderConfigurer channelBuilderConfigurer,
                                    final GrpcChannelConfigurer channelConfigurer) {
        this.clientPropertiesMap = clientPropertiesMap;
        this.inProcessChannelFactory = new InProcessChannelFactory(clientPropertiesMap, channelBuilderConfigurer, channelConfigurer);
        this.simpleChannelFactory = new SimpleChannelFactory(clientPropertiesMap, channelBuilderConfigurer, channelConfigurer);
        this.tlsChannelFactory = new TlsChannelFactory(clientPropertiesMap, channelBuilderConfigurer, channelConfigurer);
        this.customChannelFactory = applicationContext.getBean(CustomChannelFactory.class);
    }

    @Override
    public ManagedChannel createChannel(final String name, List<ClientInterceptor> interceptors) {
        GrpcClientProperties clientProperties = clientPropertiesMap.getClient(name);

        ManagedChannel channel;

        if (GrpcClientProperties.SERVER_MODEL_IN_PROCESS.equals(clientProperties.getModel())) {
            channel = inProcessChannelFactory.createChannel(name, interceptors);
        } else if (GrpcClientProperties.SERVER_MODEL_SIMPLE.equals(clientProperties.getModel())) {
            channel = simpleChannelFactory.createChannel(name, interceptors);
        } else if (GrpcClientProperties.SERVER_MODEL_TLS.equals(clientProperties.getModel())) {
            channel = tlsChannelFactory.createChannel(name, interceptors);
        } else if (GrpcClientProperties.SERVER_MODEL_CUSTOM.equals(clientProperties.getModel())) {
            channel = customChannelFactory.createChannel(name, interceptors);
        } else {
            throw new RuntimeException("Failed to create GrpcClient for name:" + name);
        }

        DisposableManagedChannel disposableManagedChannel = new DisposableManagedChannel(channel);
        return disposableManagedChannel;
    }

    @Override
    public void close() {

    }
}
