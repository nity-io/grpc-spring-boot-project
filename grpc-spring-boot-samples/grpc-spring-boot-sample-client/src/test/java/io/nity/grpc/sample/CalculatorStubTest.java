package io.nity.grpc.sample;

import io.grpc.BindableService;
import io.grpc.stub.StreamObserver;
import io.nity.grpc.CalculatorGrpc;
import io.nity.grpc.CalculatorOuterClass;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;

public class CalculatorStubTest extends StubTestBase {

    @Autowired
    private CalculatorGrpc.CalculatorBlockingStub calculatorBlockingStub;

    @Override
    protected BindableService makeServiceImpl() {
        CalculatorGrpc.CalculatorImplBase serviceImpl = mock(CalculatorGrpc.CalculatorImplBase.class, delegatesTo(new CalculatorGrpc.CalculatorImplBase() {
            public void calculate(CalculatorOuterClass.CalculatorRequest request, StreamObserver<CalculatorOuterClass.CalculatorResponse> responseObserver) {
                double result = request.getNumber1() + request.getNumber2();
                CalculatorOuterClass.CalculatorResponse calculatorResponse = CalculatorOuterClass.CalculatorResponse.newBuilder()
                        .setResult(result)
                        .build();
                responseObserver.onNext(calculatorResponse);
                responseObserver.onCompleted();
            }
        }));
        return serviceImpl;
    }

    @Test
    public void testCalculator() {
        //test 1+2
        CalculatorOuterClass.CalculatorRequest calculatorRequest = CalculatorOuterClass.CalculatorRequest.newBuilder()
                .setNumber1(1)
                .setNumber2(2)
                .setOperation(CalculatorOuterClass.CalculatorRequest.OperationType.ADD)
                .build();
        CalculatorOuterClass.CalculatorResponse response = calculatorBlockingStub.calculate(calculatorRequest);
        double result = response.getResult();

        //验证返回结果 第三个参数delta可理解为允许偏差范围
        Assert.assertEquals(3, result, 0);

        //test 2+2
        calculatorRequest = CalculatorOuterClass.CalculatorRequest.newBuilder()
                .setNumber1(2)
                .setNumber2(2)
                .setOperation(CalculatorOuterClass.CalculatorRequest.OperationType.ADD)
                .build();
        response = calculatorBlockingStub.calculate(calculatorRequest);
        result = response.getResult();

        //验证返回结果
        Assert.assertEquals(4, result, 0);

        //test 3*3 由于服务端都是加法 所以等于6
        calculatorRequest = CalculatorOuterClass.CalculatorRequest.newBuilder()
                .setNumber1(3)
                .setNumber2(3)
                .setOperation(CalculatorOuterClass.CalculatorRequest.OperationType.ADD)
                .build();
        response = calculatorBlockingStub.calculate(calculatorRequest);
        result = response.getResult();

        //验证返回结果
        Assert.assertEquals(6, result, 0);
    }

}
