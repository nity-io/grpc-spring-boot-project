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

package io.nity.grpc.client.channel;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.TimeUnit;

/**
 * 优雅关闭gRPC连接
 */
@Slf4j
public class DisposableManagedChannel extends ManagedChannel implements DisposableBean {

    private ManagedChannel channel;

    public DisposableManagedChannel(ManagedChannel channel) {
        this.channel = channel;
    }

    @Override
    public <RequestT, ResponseT> ClientCall<RequestT, ResponseT> newCall(MethodDescriptor<RequestT, ResponseT> methodDescriptor, CallOptions callOptions) {
        return channel.newCall(methodDescriptor, callOptions);
    }

    @Override
    public String authority() {
        return channel.authority();
    }

    @Override
    public void destroy() throws Exception {
        log.info("Shutting down gRPC channel ...");
        log.info("awaitTermination 5 sec.");
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    @Override
    public ManagedChannel shutdown() {
        return channel.shutdown();
    }

    @Override
    public boolean isShutdown() {
        return channel.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return channel.isTerminated();
    }

    @Override
    public ManagedChannel shutdownNow() {
        return channel.shutdownNow();
    }

    @Override
    public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
        return channel.awaitTermination(l, timeUnit);
    }
}
