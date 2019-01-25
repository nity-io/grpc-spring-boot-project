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

import java.util.Collections;
import java.util.List;

public interface GrpcChannelFactory extends AutoCloseable {

    default ManagedChannel createChannel(final String name) {
        return createChannel(name, Collections.emptyList());
    }

    ManagedChannel createChannel(String name, List<ClientInterceptor> interceptors);

    @Override
    void close();

}