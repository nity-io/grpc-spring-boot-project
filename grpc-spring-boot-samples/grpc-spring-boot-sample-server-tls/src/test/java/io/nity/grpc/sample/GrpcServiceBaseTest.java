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

package io.nity.grpc.sample;

import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.nity.grpc.server.context.LocalRunningGrpcPort;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.concurrent.TimeUnit;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SampleServerTlsApp.class)
public abstract class GrpcServiceBaseTest {

    @Autowired
    protected Environment environment;

    @LocalRunningGrpcPort
    private int port;

    protected ManagedChannel channel;

    private static final String trustCertCollectionFilePath = "/tmp/sslcert/ca.crt";

    @Before
    public void setUp() throws Exception {
        String host = environment.getProperty("grpc.server.host");

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

        SslContext sslContext = buildSslContext(trustCertCollectionFilePath);

        channel = NettyChannelBuilder.forAddress(host, port)
                .negotiationType(NegotiationType.TLS)
                .sslContext(sslContext)
                .build();
    }

    @After
    public void teardown() throws Exception {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    private static SslContext buildSslContext(String trustCertCollectionFilePath) throws SSLException {
        SslContextBuilder builder = GrpcSslContexts.forClient();
        if (trustCertCollectionFilePath != null) {
            builder.trustManager(new File(trustCertCollectionFilePath));
        }

        builder.protocols("TLSv1.2");
        return builder.build();
    }

}
