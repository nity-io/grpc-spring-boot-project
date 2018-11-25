package io.nity.grpc.sample.grpc.service;

import io.nity.grpc.sample.service.GreeterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.nity.grpc.GrpcService;

import io.nity.grpc.GreeterGrpc;
import io.nity.grpc.GreeterOuterClass;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * gRPC服务端实现，接收到数据后可交给业务service处理
 */
@GrpcService
public class GreeterGrpcService extends GreeterGrpc.GreeterImplBase {
    private static final Logger log = LoggerFactory.getLogger(CalculatorGrpcService.class);

    @Autowired
    private GreeterService greeterService;

    @Override
    public void sayHello(GreeterOuterClass.HelloRequest request, StreamObserver<GreeterOuterClass.HelloReply> responseObserver) {
        log.info("GreeterGrpcService_sayHello_request:{}", request.toString());

        String message = greeterService.sayHello(request.getName());

        final GreeterOuterClass.HelloReply.Builder replyBuilder = GreeterOuterClass.HelloReply.newBuilder().setMessage(message);
        GreeterOuterClass.HelloReply response = replyBuilder.build();

        log.info("GreeterGrpcService_sayHello_response:{}", response.toString());

        responseObserver.onNext(replyBuilder.build());
        responseObserver.onCompleted();
    }
}
