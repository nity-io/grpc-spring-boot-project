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

package io.nity.grpc.sample.service;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.nity.grpc.client.inject.GrpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 可以按平常的方式使用service
 */
@Slf4j
@Service
public class GreeterService {

    @GrpcClient("default")
    private GreeterGrpc.GreeterBlockingStub greeterBlockingStub;

    public String sayHello(String name) {
        log.info("GreeterService_sayHello name:{}", name);

        HelloReply response;

        String user = "World";
        HelloRequest request = HelloRequest.newBuilder()
                .setName(user)
                .build();

        log.info("greet sent request ...");
        response = greeterBlockingStub.sayHello(request);
        log.info("greet receive response ...");

        return response.getMessage();
    }

}