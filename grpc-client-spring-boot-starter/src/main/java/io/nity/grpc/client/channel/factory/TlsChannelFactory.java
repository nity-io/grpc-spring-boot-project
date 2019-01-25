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

import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.nity.grpc.client.channel.configurer.GrpcChannelBuilderConfigurer;
import io.nity.grpc.client.channel.configurer.GrpcChannelConfigurer;
import io.nity.grpc.client.config.GrpcClientProperties;
import io.nity.grpc.client.config.GrpcClientPropertiesMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLException;
import java.io.File;

@Slf4j
public class TlsChannelFactory extends AbstractChannelFactory<NettyChannelBuilder> {


    public TlsChannelFactory(final GrpcClientPropertiesMap clientPropertiesMap,
                             final GrpcChannelBuilderConfigurer channelBuilderConfigurer,
                             final GrpcChannelConfigurer channelConfigurer) {
        super(clientPropertiesMap, channelBuilderConfigurer, channelConfigurer);
    }

    @Override
    protected NettyChannelBuilder newChannelBuilder(final String name, final GrpcClientProperties clientProperties) {
        String host = clientProperties.getHost();
        int port = clientProperties.getPort();

        String trustCertCollectionFilePath = clientProperties.getTrustCertCollectionFilePath();

        if (!StringUtils.hasText(host)) {
            log.error("please config required property [host] for Tls model");
            throw new RuntimeException("Failed to create Tls channel");
        }
        if (!StringUtils.hasText(trustCertCollectionFilePath)) {
            log.error("please config required property [trustCertCollectionFilePath] for Tls model");
            throw new RuntimeException("Failed to create Tls channel");
        }

        log.info("will create channel with tls");
        log.info("creating channel on {}:{}", host, port);

        SslContext sslContext;
        try {
            sslContext = buildSslContext(trustCertCollectionFilePath, null, null);
        } catch (SSLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        NettyChannelBuilder nettyChannelBuilder = NettyChannelBuilder.forAddress(host, port)
                .negotiationType(NegotiationType.TLS)
                .sslContext(sslContext);
        return nettyChannelBuilder;
    }

    private static SslContext buildSslContext(String trustCertCollectionFilePath, String clientCertChainFilePath, String clientPrivateKeyFilePath) throws SSLException {
        SslContextBuilder builder = GrpcSslContexts.forClient();
        if (trustCertCollectionFilePath != null) {
            builder.trustManager(new File(trustCertCollectionFilePath));
        }
        if (clientCertChainFilePath != null && clientPrivateKeyFilePath != null) {
            builder.keyManager(new File(clientCertChainFilePath), new File(clientPrivateKeyFilePath));
        }
        builder.protocols("TLSv1.2");
        return builder.build();
    }

}
