package io.nity.grpc.sample.web;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class GreeterController {

    @Autowired
    private GreeterGrpc.GreeterBlockingStub greeterBlockingStub;

    @RequestMapping(value = {"/greet"})
    public String greet() {
        HelloReply response;

        String user = "World";
        HelloRequest request = HelloRequest.newBuilder()
                .setName(user)
                .build();

        log.info("greet sent request ...");
        response = greeterBlockingStub.sayHello(request);
        log.info("greet receive response ...");

        return response.getMessage();
    }


}
