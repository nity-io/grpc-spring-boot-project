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

package io.nity.grpc.sample.grpc.service;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.stub.StreamObserver;
import io.nity.grpc.GrpcService;
import io.nity.grpc.sample.service.GreeterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * gRPC服务端实现，接收到数据后可交给业务service处理
 */
@Slf4j
@GrpcService
public class GreeterGrpcService extends GreeterGrpc.GreeterImplBase {

    @Autowired
    private GreeterService greeterService;

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        log.info("GreeterGrpcService_sayHello_request:{}", request.toString());

        String message = greeterService.sayHello(request.getName());

        final HelloReply.Builder replyBuilder = HelloReply.newBuilder().setMessage(message);
        HelloReply response = replyBuilder.build();

        log.info("GreeterGrpcService_sayHello_response:{}", response.toString());

        responseObserver.onNext(replyBuilder.build());
        responseObserver.onCompleted();
    }

}
