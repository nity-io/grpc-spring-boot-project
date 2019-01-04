package io.nity.grpc.sample.grpc.service;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.stub.StreamObserver;
import io.nity.grpc.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * gRPC服务端实现，接收到数据后可交给业务service处理
 */
@GrpcService
public class GreeterGrpcService extends GreeterGrpc.GreeterImplBase {
    private static final Logger log = LoggerFactory.getLogger(GreeterGrpcService.class);

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
