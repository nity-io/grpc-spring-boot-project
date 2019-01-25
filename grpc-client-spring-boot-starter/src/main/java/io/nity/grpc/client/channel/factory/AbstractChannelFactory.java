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

import com.google.common.collect.Lists;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.nity.grpc.client.channel.configurer.GrpcChannelBuilderConfigurer;
import io.nity.grpc.client.channel.configurer.GrpcChannelConfigurer;
import io.nity.grpc.client.config.GrpcClientProperties;
import io.nity.grpc.client.config.GrpcClientPropertiesMap;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import javax.annotation.concurrent.GuardedBy;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

@Slf4j
public abstract class AbstractChannelFactory<T extends ManagedChannelBuilder<T>> implements GrpcChannelFactory {

    private final GrpcClientPropertiesMap clientPropertiesMap;
    protected final GrpcChannelBuilderConfigurer channelBuilderConfigurer;
    protected final GrpcChannelConfigurer channelConfigurer;

    /**
     * ManagedChannels should be reused to allow connection reuse.
     */
    @GuardedBy("this")
    private final Map<String, ManagedChannel> channels = new ConcurrentHashMap<>();
    private boolean shutdown = false;

    public AbstractChannelFactory(final GrpcClientPropertiesMap clientPropertiesMap, final GrpcChannelBuilderConfigurer channelBuilderConfigurer, final GrpcChannelConfigurer channelConfigurer) {
        this.clientPropertiesMap = requireNonNull(clientPropertiesMap, "clientPropertiesMap");
        this.channelBuilderConfigurer = requireNonNull(channelBuilderConfigurer, "channelBuilderConfigurer");
        this.channelConfigurer = requireNonNull(channelConfigurer, "channelConfigurer");
    }

    @Override
    public final ManagedChannel createChannel(final String name) {
        return createChannel(name, Collections.emptyList());
    }

    @Override
    public ManagedChannel createChannel(final String name, final List<ClientInterceptor> customInterceptors) {
        final ManagedChannel channel;

        synchronized (this) {
            if (this.shutdown) {
                throw new IllegalStateException("GrpcChannelFactory is already closed!");
            }
            channel = this.channels.computeIfAbsent(name, this::newManagedChannel);
        }

        final List<ClientInterceptor> interceptors = Lists.newArrayList();

        if (!customInterceptors.isEmpty()) {
            interceptors.addAll(customInterceptors);
        }

        ClientInterceptors.intercept(channel, interceptors);

        return channel;
    }

    protected abstract T newChannelBuilder(final String name, final GrpcClientProperties clientProperties);

    protected ManagedChannel newManagedChannel(final String name) {
        GrpcClientProperties clientProperties = getPropertiesFor(name);

        final T builder = newChannelBuilder(name, clientProperties);

        configure(builder, name);

        ManagedChannel channel = builder.build();

        channelConfigurer.configure(channel, name);

        return channel;
    }

    protected final GrpcClientProperties getPropertiesFor(final String name) {
        return this.clientPropertiesMap.getClient(name);
    }

    protected void configure(final T builder, final String name) {
        channelBuilderConfigurer.configure(builder, name);
    }

    @Override
    @PreDestroy
    public synchronized void close() {
        if (this.shutdown) {
            return;
        }
        this.shutdown = true;
    }

}
