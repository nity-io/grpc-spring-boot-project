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

import io.grpc.BindableService;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.stub.StreamObserver;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;

public class GreeterControllerTest extends WebTestBase {

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
    public void testSayHello() throws Exception {
        String url = "http://localhost:8080/greet";

        String response = restTemplate.getForEntity(url, String.class).getBody();

        Assert.assertEquals("Hello World", response);
    }

}
