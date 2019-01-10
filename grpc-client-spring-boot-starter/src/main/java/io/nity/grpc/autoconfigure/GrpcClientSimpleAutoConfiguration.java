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

package io.nity.grpc.autoconfigure;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.nity.grpc.DisposableManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

@Slf4j
@AutoConfigureOrder
public class GrpcClientSimpleAutoConfiguration {

    @Autowired
    private GrpcClientProperties clientProperties;

    @Bean
    @ConditionalOnProperty(value = "grpc.client.model", havingValue = GrpcClientProperties.SERVER_MODEL_SIMPLE)
    public DisposableManagedChannel getChannel() {
        int port = clientProperties.getPort();
        ManagedChannel channel;
        String host = clientProperties.getHost();

        if (!StringUtils.hasText(host)) {
            log.error("please config required property [host] for simple model");
            throw new RuntimeException("Failed to create channel without tls");
        }

        log.info("will create channel without tls. recommend only use in internal service");
        log.info("creating channel on {}:{}", host, port);

        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        DisposableManagedChannel disposableManagedChannel = new DisposableManagedChannel(channel);
        return disposableManagedChannel;
    }

}
