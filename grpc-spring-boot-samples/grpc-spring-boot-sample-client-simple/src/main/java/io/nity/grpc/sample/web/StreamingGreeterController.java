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

import com.google.common.collect.Lists;
import io.grpc.examples.manualflowcontrol.StreamingGreeterGrpc;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;
import io.grpc.stub.StreamObserver;
import io.nity.grpc.client.inject.GrpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.List;

@Slf4j
@RestController
public class StreamingGreeterController {

    @GrpcClient("default")
    private StreamingGreeterGrpc.StreamingGreeterStub streamingGreeterStub;

    @RequestMapping(value = {"/streamingGreet"})
    public String greet() {
        String user = "World";

        log.info("greet sent request ...");

        List<String> names = Lists.newArrayList();
        names.add(user);

        ClientResponseObserver<io.grpc.examples.manualflowcontrol.HelloRequest, io.grpc.examples.manualflowcontrol.HelloReply> clientResponseObserver =
                new ClientResponseObserver<io.grpc.examples.manualflowcontrol.HelloRequest, io.grpc.examples.manualflowcontrol.HelloReply>() {

                    ClientCallStreamObserver<io.grpc.examples.manualflowcontrol.HelloRequest> requestStream;

                    @Override
                    public void beforeStart(final ClientCallStreamObserver<io.grpc.examples.manualflowcontrol.HelloRequest> requestStream) {
                        this.requestStream = requestStream;
                        // Set up manual flow control for the response stream. It feels backwards to configure the response
                        // stream's flow control using the request stream's observer, but this is the way it is.
                        requestStream.disableAutoInboundFlowControl();

                        // Set up a back-pressure-aware producer for the request stream. The onReadyHandler will be invoked
                        // when the consuming side has enough buffer space to receive more messages.
                        //
                        // Messages are serialized into a transport-specific transmit buffer. Depending on the size of this buffer,
                        // MANY messages may be buffered, however, they haven't yet been sent to the server. The server must call
                        // request() to pull a buffered message from the client.
                        //
                        // Note: the onReadyHandler's invocation is serialized on the same thread pool as the incoming
                        // StreamObserver'sonNext(), onError(), and onComplete() handlers. Blocking the onReadyHandler will prevent
                        // additional messages from being processed by the incoming StreamObserver. The onReadyHandler must return
                        // in a timely manor or else message processing throughput will suffer.
                        requestStream.setOnReadyHandler(new Runnable() {
                            // An iterator is used so we can pause and resume iteration of the request data.
                            Iterator<String> iterator = names.iterator();

                            @Override
                            public void run() {
                                // Start generating values from where we left off on a non-gRPC thread.
                                while (requestStream.isReady()) {
                                    if (iterator.hasNext()) {
                                        // Send more messages if there are more messages to send.
                                        String name = iterator.next();
                                        log.info("--> " + name);
                                        io.grpc.examples.manualflowcontrol.HelloRequest request = io.grpc.examples.manualflowcontrol.HelloRequest.newBuilder().setName(name).build();
                                        requestStream.onNext(request);
                                    } else {
                                        // Signal completion if there is nothing left to send.
                                        requestStream.onCompleted();
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onNext(io.grpc.examples.manualflowcontrol.HelloReply value) {
                        log.info("<-- " + value.getMessage());
                        // Signal the sender to send one message.
                        requestStream.request(1);
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onCompleted() {
                        log.info("All Done");
                    }
                };

        StreamObserver<io.grpc.examples.manualflowcontrol.HelloRequest> helloRequestStreamObserver = streamingGreeterStub.sayHelloStreaming(clientResponseObserver);

        return "done";
    }

}
