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

package io.nity.grpc.sample.web;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.nity.grpc.client.inject.GrpcClient;
import io.nity.grpc.sample.interceptor.SampleInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class GreeterController {

    @GrpcClient(value = "default", interceptors = SampleInterceptor.class)
    private GreeterGrpc.GreeterBlockingStub greeterBlockingStub;

    @RequestMapping(value = {"/greet"})
    public String greet() {
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
