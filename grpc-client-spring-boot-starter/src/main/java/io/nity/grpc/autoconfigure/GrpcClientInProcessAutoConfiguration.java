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
import io.grpc.inprocess.InProcessChannelBuilder;
import io.nity.grpc.DisposableManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

@Slf4j
@AutoConfigureOrder
public class GrpcClientInProcessAutoConfiguration {

    @Autowired
    private GrpcClientProperties clientProperties;

    @Bean
    @ConditionalOnProperty(value = "grpc.client.model", havingValue = GrpcClientProperties.SERVER_MODEL_IN_PROCESS)
    public DisposableManagedChannel getChannel() {
        ManagedChannel channel;

        String inProcessServerName = clientProperties.getInProcessServerName();

        if (!StringUtils.hasText(inProcessServerName)) {
            log.error("please config required property [inProcessServerName] for InProcess model");
            throw new RuntimeException("Failed to create inProcessChannel");
        }

        log.warn("will create InProcessChannel. Please only use in testing");
        channel = InProcessChannelBuilder.forName(inProcessServerName).directExecutor().build();

        DisposableManagedChannel disposableManagedChannel = new DisposableManagedChannel(channel);
        return disposableManagedChannel;
    }

}
