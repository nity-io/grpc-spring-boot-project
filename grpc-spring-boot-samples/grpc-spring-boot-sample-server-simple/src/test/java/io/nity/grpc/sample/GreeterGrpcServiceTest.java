package io.nity.grpc.sample;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
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
        HelloRequest request = HelloRequest.newBuilder()
                .setName(user)
                .build();
        HelloReply reply = this.greeterBlockingStub.sayHello(request);
        String message = reply.getMessage();

        //验证返回结果
        Assert.assertEquals("Hello " + user, message);

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
