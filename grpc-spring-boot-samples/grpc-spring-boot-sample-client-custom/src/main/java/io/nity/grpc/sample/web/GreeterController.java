package io.nity.grpc.sample.web;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreeterController {
    private static final Logger logger = LoggerFactory.getLogger(GreeterController.class);

    @Autowired
    private GreeterGrpc.GreeterBlockingStub greeterBlockingStub;

    @RequestMapping(value = {"/greet"})
    public String greet() {
        HelloReply response;

        String user = "World";
        HelloRequest request = HelloRequest.newBuilder()
                .setName(user)
                .build();

        logger.info("greet sent request ...");
        response = greeterBlockingStub.sayHello(request);
        logger.info("greet receive response ...");

        return response.getMessage();
    }


}
