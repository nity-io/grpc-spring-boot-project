package io.nity.grpc.sample;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.nity.grpc.sample.web.GreeterController;
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
@WebMvcTest(GreeterController.class)
public class GreeterControllerTest {

    @Autowired
    private MockMvc mvc;

    /**
     * 为了mock final的gRPC stub，需要mockito-inline 2.*版本
     */
    @MockBean
    private GreeterGrpc.GreeterBlockingStub greeterBlockingStub;

    @Test
    public void testSayHello() throws Exception {
        String user = "World";
        HelloRequest request = HelloRequest.newBuilder()
                .setName(user)
                .build();
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + user).build();

        given(this.greeterBlockingStub.sayHello(request)).willReturn(reply);

        this.mvc.perform(get("/greet").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk()).
                andExpect(content().string("Hello World"));
    }

}
