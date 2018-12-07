package io.nity.grpc.sample;

import io.nity.grpc.CalculatorGrpc;
import io.nity.grpc.CalculatorOuterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CalculatorGrpcServiceTest extends GrpcServiceBaseTest {

    private CalculatorGrpc.CalculatorBlockingStub calculatorBlockingStub;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        calculatorBlockingStub = CalculatorGrpc.newBlockingStub(channel);
    }

    @Test
    public void testCalculate() {
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

        //test 2-2
        calculatorRequest = CalculatorOuterClass.CalculatorRequest.newBuilder()
                .setNumber1(2)
                .setNumber2(2)
                .setOperation(CalculatorOuterClass.CalculatorRequest.OperationType.SUBTRACT)
                .build();
        response = calculatorBlockingStub.calculate(calculatorRequest);
        result = response.getResult();

        //验证返回结果
        Assert.assertEquals(0, result, 0);

        //test 3*3
        calculatorRequest = CalculatorOuterClass.CalculatorRequest.newBuilder()
                .setNumber1(3)
                .setNumber2(3)
                .setOperation(CalculatorOuterClass.CalculatorRequest.OperationType.MULTIPLY)
                .build();
        response = calculatorBlockingStub.calculate(calculatorRequest);
        result = response.getResult();

        //验证返回结果
        Assert.assertEquals(9, result, 0);

        //test 10/3
        calculatorRequest = CalculatorOuterClass.CalculatorRequest.newBuilder()
                .setNumber1(10)
                .setNumber2(3)
                .setOperation(CalculatorOuterClass.CalculatorRequest.OperationType.DIVIDE)
                .build();
        response = calculatorBlockingStub.calculate(calculatorRequest);
        result = response.getResult();

        //验证返回结果
        Assert.assertEquals(3.3, result, 0.1);
    }

}
