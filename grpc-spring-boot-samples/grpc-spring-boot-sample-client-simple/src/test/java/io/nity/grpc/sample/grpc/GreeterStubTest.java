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

package io.nity.grpc.sample.grpc;

import io.grpc.BindableService;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.stub.StreamObserver;
import io.nity.grpc.client.inject.GrpcClient;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;

public class GreeterStubTest extends StubTestBase {

    @GrpcClient("default")
    private GreeterGrpc.GreeterBlockingStub greeterBlockingStub;

    @Override
    protected BindableService makeServiceImpl() {
        GreeterGrpc.GreeterImplBase serviceImpl = mock(GreeterGrpc.GreeterImplBase.class, delegatesTo(new GreeterGrpc.GreeterImplBase() {
            public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
                HelloReply reply = HelloReply.newBuilder()
                        .setMessage("Hello " + req.getName())
                        .build();
                responseObserver.onNext(reply);
                responseObserver.onCompleted();
            }
        }));
        return serviceImpl;
    }

    @Test
    public void testSayHello() {
        //test Hello World
        String user = "World";
        HelloRequest request = HelloRequest.newBuilder()
                .setName(user)
                .build();
        HelloReply reply = greeterBlockingStub.sayHello(request);
        String message = reply.getMessage();

        //验证返回结果
        Assert.assertEquals("Hello " + user, message);

        //验证server端执行方法和参数 这段在mockito-core可以跑通 在mockito-inline 2.*版本跑不通
        //ArgumentCaptor<HelloRequest> requestCaptor = ArgumentCaptor.forClass(HelloRequest.class);
        //verify(serviceImpl).sayHello(requestCaptor.capture(), Matchers.any());
        //assertEquals(user, requestCaptor.getValue().getName());

        //test Hello LiLei
        user = " LiLei";
        request = HelloRequest.newBuilder()
                .setName(user)
                .build();
        reply = greeterBlockingStub.sayHello(request);
        message = reply.getMessage();

        //验证返回结果
        Assert.assertEquals("Hello " + user, message);
    }

}
