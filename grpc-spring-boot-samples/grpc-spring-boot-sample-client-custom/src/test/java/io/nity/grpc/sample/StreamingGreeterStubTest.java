package io.nity.grpc.sample;

import io.grpc.BindableService;
import io.grpc.Status;
import io.grpc.examples.manualflowcontrol.StreamingGreeterGrpc;
import io.grpc.examples.manualflowcontrol.StreamingGreeterGrpc.StreamingGreeterImplBase;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;

public class StreamingGreeterStubTest extends StubTestBase {
    private static final Logger log = LoggerFactory.getLogger(StreamingGreeterStubTest.class);

    @Autowired
    private StreamingGreeterGrpc.StreamingGreeterStub streamingGreeterStub;

    @Override
    protected BindableService makeServiceImpl() {
        StreamingGreeterImplBase delegate = new StreamingGreeterImplBase() {
            @Override
            public io.grpc.stub.StreamObserver<io.grpc.examples.manualflowcontrol.HelloRequest> sayHelloStreaming(io.grpc.stub.StreamObserver<io.grpc.examples.manualflowcontrol.HelloReply> responseObserver) {
                // Set up manual flow control for the request stream. It feels backwards to configure the request
                // stream's flow control using the response stream's observer, but this is the way it is.
                final ServerCallStreamObserver<io.grpc.examples.manualflowcontrol.HelloReply> serverCallStreamObserver =
                        (ServerCallStreamObserver<io.grpc.examples.manualflowcontrol.HelloReply>) responseObserver;
                serverCallStreamObserver.disableAutoInboundFlowControl();

                // Guard against spurious onReady() calls caused by a race between onNext() and onReady(). If the transport
                // toggles isReady() from false to true while onNext() is executing, but before onNext() checks isReady(),
                // request(1) would be called twice - once by onNext() and once by the onReady() scheduled during onNext()'s
                // execution.
                final AtomicBoolean wasReady = new AtomicBoolean(false);

                // Set up a back-pressure-aware consumer for the request stream. The onReadyHandler will be invoked
                // when the consuming side has enough buffer space to receive more messages.
                //
                // Note: the onReadyHandler's invocation is serialized on the same thread pool as the incoming StreamObserver's
                // onNext(), onError(), and onComplete() handlers. Blocking the onReadyHandler will prevent additional messages
                // from being processed by the incoming StreamObserver. The onReadyHandler must return in a timely manor or else
                // message processing throughput will suffer.
                serverCallStreamObserver.setOnReadyHandler(new Runnable() {
                    @Override
                    public void run() {
                        if (serverCallStreamObserver.isReady() && wasReady.compareAndSet(false, true)) {
                            log.info("READY");
                            // Signal the request sender to send one message. This happens when isReady() turns true, signaling that
                            // the receive buffer has enough free space to receive more messages. Calling request() serves to prime
                            // the message pump.
                            serverCallStreamObserver.request(1);
                        }
                    }
                });

                // Give gRPC a StreamObserver that can observe and process incoming requests.
                return new StreamObserver<io.grpc.examples.manualflowcontrol.HelloRequest>() {
                    @Override
                    public void onNext(io.grpc.examples.manualflowcontrol.HelloRequest request) {
                        // Process the request and send a response or an error.
                        try {
                            // Accept and enqueue the request.
                            String name = request.getName();
                            log.info("--> " + name);

                            log.info("StreamingGreeterGrpcService_sayHelloStreaming_request:{}", request.toString());

                            // Simulate server "work"
                            Thread.sleep(100);

                            // Send a response.
                            String message = "Hello " + name;
                            log.info("<-- " + message);
                            io.grpc.examples.manualflowcontrol.HelloReply reply = io.grpc.examples.manualflowcontrol.HelloReply.newBuilder().setMessage(message).build();
                            responseObserver.onNext(reply);

                            log.info("StreamingGreeterGrpcService_sayHelloStreaming_reply:{}", reply.toString());

                            // Check the provided ServerCallStreamObserver to see if it is still ready to accept more messages.
                            if (serverCallStreamObserver.isReady()) {
                                // Signal the sender to send another request. As long as isReady() stays true, the server will keep
                                // cycling through the loop of onNext() -> request()...onNext() -> request()... until either the client
                                // runs out of messages and ends the loop or the server runs out of receive buffer space.
                                //
                                // If the server runs out of buffer space, isReady() will turn false. When the receive buffer has
                                // sufficiently drained, isReady() will turn true, and the serverCallStreamObserver's onReadyHandler
                                // will be called to restart the message pump.
                                serverCallStreamObserver.request(1);
                            } else {
                                // If not, note that back-pressure has begun.
                                wasReady.set(false);
                            }
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                            responseObserver.onError(
                                    Status.UNKNOWN.withDescription("Error handling request").withCause(throwable).asException());
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        // End the response stream if the client presents an error.
                        t.printStackTrace();
                        responseObserver.onCompleted();
                    }

                    @Override
                    public void onCompleted() {
                        // Signal the end of work when the client ends the request stream.
                        log.info("COMPLETED");
                        responseObserver.onCompleted();
                    }
                };

            }
        };

        StreamingGreeterImplBase serviceImpl = mock(StreamingGreeterImplBase.class, delegatesTo(delegate));
        return serviceImpl;
    }

    private static List<String> names() {
        return Arrays.asList(
                "Sophia",
                "Jackson"
        );
    }

    @Test
    public void testSayHello() throws InterruptedException {
        final CountDownLatch done = new CountDownLatch(1);
        final AtomicInteger errorCount = new AtomicInteger(0);

        // When using manual flow-control and back-pressure on the client, the ClientResponseObserver handles both
        // request and response streams.
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
                            Iterator<String> iterator = names().iterator();

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
                        done.countDown();
                        errorCount.incrementAndGet();
                    }

                    @Override
                    public void onCompleted() {
                        log.info("All Done");
                        done.countDown();
                    }
                };

        // Note: clientResponseObserver is handling both request and response stream processing.
        streamingGreeterStub.sayHelloStreaming(clientResponseObserver);

        done.await();

        if(errorCount.intValue() > 0){
            Assert.fail(errorCount + " Error when test sayHelloStreaming");
        }
    }

}
