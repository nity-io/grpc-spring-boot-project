package io.nity.grpc.sample.grpc.service;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.stub.StreamObserver;
import io.nity.grpc.GrpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * gRPC服务端实现，接收到数据后可交给业务service处理
 */
@Slf4j
@GrpcService
public class GreeterGrpcService extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        log.info("GreeterGrpcService_sayHello_request:{}", request.toString());

        String name = request.getName();
        String message = "Hello " + name;

        final HelloReply.Builder replyBuilder = HelloReply.newBuilder().setMessage(message);
        HelloReply response = replyBuilder.build();

        log.info("GreeterGrpcService_sayHello_response:{}", response.toString());

        responseObserver.onNext(replyBuilder.build());
        responseObserver.onCompleted();
    }

}
