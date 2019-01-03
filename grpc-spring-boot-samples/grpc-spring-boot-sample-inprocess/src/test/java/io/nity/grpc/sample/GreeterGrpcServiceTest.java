package io.nity.grpc.sample;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.nity.grpc.DisposableManagedChannel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SampleInProcessApp.class)
public class GreeterGrpcServiceTest {

    @Autowired
    private GreeterGrpc.GreeterBlockingStub greeterBlockingStub;

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
