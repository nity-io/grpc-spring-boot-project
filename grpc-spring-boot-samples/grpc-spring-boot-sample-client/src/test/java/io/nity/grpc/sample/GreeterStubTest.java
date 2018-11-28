package io.nity.grpc.sample;

import io.grpc.BindableService;
import io.grpc.stub.StreamObserver;
import io.nity.grpc.GreeterGrpc;
import io.nity.grpc.GreeterOuterClass;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;

public class GreeterStubTest extends StubTestBase {

    @Autowired
    private GreeterGrpc.GreeterBlockingStub greeterBlockingStub;

    @Override
    protected BindableService makeServiceImpl() {
        GreeterGrpc.GreeterImplBase serviceImpl = mock(GreeterGrpc.GreeterImplBase.class, delegatesTo(new GreeterGrpc.GreeterImplBase() {
            public void sayHello(GreeterOuterClass.HelloRequest req, StreamObserver<GreeterOuterClass.HelloReply> responseObserver) {
                GreeterOuterClass.HelloReply reply = GreeterOuterClass.HelloReply.newBuilder()
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

        //test Hello LiLei
        user = " LiLei";
        request = GreeterOuterClass.HelloRequest.newBuilder()
                .setName(user)
                .build();
        response = greeterBlockingStub.sayHello(request);
        message = response.getMessage();

        //验证返回结果
        Assert.assertEquals("Hello " + user, message);
    }

}
