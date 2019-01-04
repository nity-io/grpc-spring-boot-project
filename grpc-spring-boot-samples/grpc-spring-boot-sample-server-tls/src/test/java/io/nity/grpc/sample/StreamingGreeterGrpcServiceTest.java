package io.nity.grpc.sample;

import io.grpc.examples.manualflowcontrol.HelloReply;
import io.grpc.examples.manualflowcontrol.HelloRequest;
import io.grpc.examples.manualflowcontrol.StreamingGreeterGrpc;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class StreamingGreeterGrpcServiceTest extends GrpcServiceBaseTest {

    private StreamingGreeterGrpc.StreamingGreeterStub streamingGreeterStub;

    private static List<String> names() {
        return Arrays.asList(
                "Sophia",
                "Jackson"
        );
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        streamingGreeterStub = StreamingGreeterGrpc.newStub(channel);
    }

    @Test
    public void testSayHello() throws InterruptedException {
        final CountDownLatch done = new CountDownLatch(1);
        final AtomicInteger errorCount = new AtomicInteger(0);

        // When using manual flow-control and back-pressure on the client, the ClientResponseObserver handles both
        // request and response streams.
        ClientResponseObserver<HelloRequest, HelloReply> clientResponseObserver =
                new ClientResponseObserver<HelloRequest, HelloReply>() {

                    ClientCallStreamObserver<HelloRequest> requestStream;

                    @Override
                    public void beforeStart(final ClientCallStreamObserver<HelloRequest> requestStream) {
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
                                        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
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
                    public void onNext(HelloReply value) {
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
