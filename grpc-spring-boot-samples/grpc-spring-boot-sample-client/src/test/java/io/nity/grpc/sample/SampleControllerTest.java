package io.nity.grpc.sample;

import io.nity.grpc.CalculatorGrpc;
import io.nity.grpc.CalculatorOuterClass;
import io.nity.grpc.GreeterGrpc;
import io.nity.grpc.GreeterOuterClass;
import io.nity.grpc.sample.web.SampleController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(SampleController.class)
public class SampleControllerTest {

    @Autowired
    private MockMvc mvc;

    /**
     * 为了mock final的gRPC stub，需要mockito-inline 2.*版本
     */
    @MockBean
    private GreeterGrpc.GreeterBlockingStub greeterBlockingStub;
    @MockBean
    private CalculatorGrpc.CalculatorBlockingStub calculatorBlockingStub;

    @Test
    public void testSayHello() throws Exception {
        String user = "World";
        GreeterOuterClass.HelloRequest request = GreeterOuterClass.HelloRequest.newBuilder()
                .setName(user)
                .build();
        GreeterOuterClass.HelloReply reply = GreeterOuterClass.HelloReply.newBuilder().setMessage("Hello " + user).build();

        given(this.greeterBlockingStub.sayHello(request)).willReturn(reply);

        this.mvc.perform(get("/greet").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk()).
                andExpect(content().string("Hello World"));
    }

    @Test
    public void testCalculate() throws Exception {
        CalculatorOuterClass.CalculatorRequest calculatorRequest = CalculatorOuterClass.CalculatorRequest.newBuilder()
                .setNumber1(1)
                .setNumber2(2)
                .setOperation(CalculatorOuterClass.CalculatorRequest.OperationType.ADD)
                .build();

        CalculatorOuterClass.CalculatorResponse calculatorResponse = CalculatorOuterClass.CalculatorResponse.newBuilder()
                .setResult(3)
                .build();
        given(this.calculatorBlockingStub.calculate(calculatorRequest)).willReturn(calculatorResponse);

        this.mvc.perform(get("/calculate").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).
                andExpect(content().string("3.0"));
    }

}
