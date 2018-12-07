package io.nity.grpc.sample;

import io.nity.grpc.GreeterGrpc;
import io.nity.grpc.GreeterOuterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GreeterGrpcServiceTest extends GrpcServiceBaseTest {

    private GreeterGrpc.GreeterBlockingStub greeterBlockingStub;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        greeterBlockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    @Test
    public void testSayHello() {
        //test Hello World
        String user = "World";
        GreeterOuterClass.HelloRequest request = GreeterOuterClass.HelloRequest.newBuilder()
                .setName(user)
                .build();
        GreeterOuterClass.HelloReply reply = this.greeterBlockingStub.sayHello(request);
        String message = reply.getMessage();

        //验证返回结果
        Assert.assertEquals("Hello " + user, message);

        //test Hello LiLei
        user = " LiLei";
        request = GreeterOuterClass.HelloRequest.newBuilder()
                .setName(user)
                .build();
        reply = greeterBlockingStub.sayHello(request);
        message = reply.getMessage();

        //验证返回结果
        Assert.assertEquals("Hello " + user, message);
    }

}
