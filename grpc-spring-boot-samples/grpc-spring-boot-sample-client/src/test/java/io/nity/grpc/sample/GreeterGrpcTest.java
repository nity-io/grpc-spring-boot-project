package io.nity.grpc.sample;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import io.nity.grpc.GreeterGrpc;
import io.nity.grpc.GreeterOuterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GreeterGrpcTest {

    private static final Logger log = LoggerFactory.getLogger(GreeterGrpcTest.class);

    @Autowired
    private Environment environment;

    @Autowired
    private GreeterGrpc.GreeterBlockingStub greeterBlockingStub;

    private static Server server;

    /**
     * This rule manages automatic graceful shutdown for the registered servers and channels at the end of test.
     */
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private final GreeterGrpc.GreeterImplBase serviceImpl = mock(GreeterGrpc.GreeterImplBase.class, delegatesTo(new GreeterGrpc.GreeterImplBase() {
        public void sayHello(GreeterOuterClass.HelloRequest req, StreamObserver<GreeterOuterClass.HelloReply> responseObserver) {
            GreeterOuterClass.HelloReply reply = GreeterOuterClass.HelloReply.newBuilder()
                    .setMessage("Hello " + req.getName())
                    .build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }));

    @Before
    public void setUp() throws Exception {
        int port = environment.getProperty("grpc.stub.port", Integer.class, 50051);
        server = ServerBuilder.forPort(port)
                .addService(serviceImpl)
                .build()
                .start();

        grpcCleanup.register(server);

        log.info("Test gRPC Server started, listening on " + port);
    }

    @Test
    public void testSayHello() {
        String user = "World";
        GreeterOuterClass.HelloRequest request = GreeterOuterClass.HelloRequest.newBuilder()
                .setName(user)
                .build();

        GreeterOuterClass.HelloReply response = greeterBlockingStub.sayHello(request);

        String message = response.getMessage();

        //验证返回结果
        Assert.assertEquals("Hello " + user, message);

        //验证server端执行方法和参数 这段在mockito-core可以跑通 在mockito-inline 2.*版本跑不通
        //ArgumentCaptor<GreeterOuterClass.HelloRequest> requestCaptor = ArgumentCaptor.forClass(GreeterOuterClass.HelloRequest.class);
        //verify(serviceImpl).sayHello(requestCaptor.capture(), Matchers.any());
        //assertEquals(user, requestCaptor.getValue().getName());
    }

}
