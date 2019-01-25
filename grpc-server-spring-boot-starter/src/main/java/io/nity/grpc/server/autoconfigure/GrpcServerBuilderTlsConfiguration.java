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
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.nity.grpc.server.GrpcService;
import io.nity.grpc.server.context.LocalRunningGrpcPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLException;
import java.io.File;
import java.net.InetSocketAddress;

@Slf4j
@Configuration
@ConditionalOnBean(annotation = GrpcService.class)
@EnableConfigurationProperties(GrpcServerProperties.class)
public class GrpcServerBuilderTlsConfiguration {

    @LocalRunningGrpcPort
    private int port;

    @Autowired
    private GrpcServerProperties serverProperties;

    @Bean
    @ConditionalOnProperty(value = "grpc.server.model", havingValue = GrpcServerProperties.SERVER_MODEL_TLS)
    public ServerBuilder getServerBuilder() throws SSLException {
        ServerBuilder serverBuilder;

        String host = serverProperties.getHost();
        String certChainFilePath = serverProperties.getCertChainFilePath();
        String privateKeyFilePath = serverProperties.getPrivateKeyFilePath();

        if (!StringUtils.hasText(host)) {
            log.error("please config required property [host] for Tls model");
            throw new RuntimeException("Failed to create Tls Server");
        }
        if (!StringUtils.hasText(certChainFilePath)) {
            log.error("please config required property [certChainFilePath] for Tls model");
            throw new RuntimeException("Failed to create Tls Server");
        }
        if (!StringUtils.hasText(privateKeyFilePath)) {
            log.error("please config required property [privateKeyFilePath] for Tls model");
            throw new RuntimeException("Failed to create Tls Server");
        }

        log.info("gRPC Server will run with tls");
        log.info("gRPC Server will listen on {}:{}", host, port);

        NettyServerBuilder nettyServerBuilder = NettyServerBuilder.forAddress(new InetSocketAddress(host, port));
        nettyServerBuilder.sslContext(buildSslContext(certChainFilePath, privateKeyFilePath, null));

        serverBuilder = nettyServerBuilder;

        return serverBuilder;
    }

    private SslContext buildSslContext(String certChainFilePath, String privateKeyFilePath, String trustCertCollectionFilePath) throws SSLException {
        File certChainFile = new File(certChainFilePath);
        File privateKeyFile = new File(privateKeyFilePath);
        log.info("loading certChainFile:{}", certChainFile.getAbsolutePath());
        log.info("loading privateKeyFile:{}", privateKeyFile.getAbsolutePath());
        SslContextBuilder sslClientContextBuilder = SslContextBuilder.forServer(certChainFile, privateKeyFile);

        if (trustCertCollectionFilePath != null) {
            File trustCertCollectionFile = new File(trustCertCollectionFilePath);
            log.info("loading trustCertCollectionFile:{}", trustCertCollectionFile.getAbsolutePath());
            sslClientContextBuilder.trustManager(trustCertCollectionFile);
            sslClientContextBuilder.clientAuth(ClientAuth.REQUIRE);
        }

        sslClientContextBuilder.protocols("TLSv1.2");

        SslContextBuilder sslContextBuilder = GrpcSslContexts.configure(sslClientContextBuilder, SslProvider.OPENSSL);
        return sslContextBuilder.build();
    }
}
