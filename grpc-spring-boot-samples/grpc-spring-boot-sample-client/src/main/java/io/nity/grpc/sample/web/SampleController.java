package io.nity.grpc.sample.web;

import io.nity.grpc.CalculatorGrpc;
import io.nity.grpc.CalculatorOuterClass;
import io.nity.grpc.GreeterGrpc;
import io.nity.grpc.GreeterOuterClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {
    private static final Logger logger = LoggerFactory.getLogger(SampleController.class);

    @Autowired
    private GreeterGrpc.GreeterBlockingStub greeterBlockingStub;

    @Autowired
    private CalculatorGrpc.CalculatorBlockingStub calculatorBlockingStub;

    @RequestMapping(value = {"/greet"})
    public String greet() {
        GreeterOuterClass.HelloReply response;

        String user = "World";
        GreeterOuterClass.HelloRequest request = GreeterOuterClass.HelloRequest.newBuilder()
                .setName(user)
                .build();

        logger.info("greet sent request ...");
        response = greeterBlockingStub.sayHello(request);
        logger.info("greet receive response ...");

        return response.getMessage();
    }

    @RequestMapping(value = {"/calculate"})
    public double calculate() {
        CalculatorOuterClass.CalculatorResponse response;

        CalculatorOuterClass.CalculatorRequest request = CalculatorOuterClass.CalculatorRequest.newBuilder()
                .setNumber1(1)
                .setNumber2(2)
                .setOperation(CalculatorOuterClass.CalculatorRequest.OperationType.ADD)
                .build();

        response = calculatorBlockingStub.calculate(request);

        return response.getResult();
    }
}
