package io.nity.grpc.sample.grpc.service;

import io.nity.grpc.sample.grpc.interceptor.RegionalGrpcInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.nity.grpc.CalculatorGrpc;
import io.nity.grpc.CalculatorOuterClass;
import io.nity.grpc.GrpcService;
import io.grpc.stub.StreamObserver;

/**
 * gRPC服务端实现，局部拦截器示例
 */
@GrpcService(interceptors = RegionalGrpcInterceptor.class)
public class CalculatorGrpcService extends CalculatorGrpc.CalculatorImplBase {
    private static final Logger log = LoggerFactory.getLogger(CalculatorGrpcService.class);

    @Override
    public void calculate(CalculatorOuterClass.CalculatorRequest request, StreamObserver<CalculatorOuterClass.CalculatorResponse> responseObserver) {
        log.info("CalculatorGrpcService_calculate_request:\n{}", request.toString());

        CalculatorOuterClass.CalculatorResponse.Builder resultBuilder = CalculatorOuterClass.CalculatorResponse.newBuilder();
        double result;

        switch (request.getOperation()) {
            case ADD:
                result = request.getNumber1() + request.getNumber2();
                resultBuilder.setResult(result);
                break;
            case SUBTRACT:
                result = request.getNumber1() - request.getNumber2();
                resultBuilder.setResult(result);
                break;
            case MULTIPLY:
                result = request.getNumber1() * request.getNumber2();
                resultBuilder.setResult(result);
                break;
            case DIVIDE:
                result = request.getNumber1() / request.getNumber2();
                resultBuilder.setResult(result);
                break;
            case UNRECOGNIZED:
                break;
            default:
                break;
        }

        CalculatorOuterClass.CalculatorResponse response = resultBuilder.build();

        log.info("CalculatorGrpcService_calculate_response:\n{}", response.toString());

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
