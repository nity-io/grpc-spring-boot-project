package io.nity.grpc.sample;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SampleServerApp.class)
public abstract class GrpcServiceBaseTest {

    @Autowired
    protected Environment environment;

    protected ManagedChannel channel;

    @Before
    public void setUp() throws Exception {
        String serverName = environment.getProperty("grpc.server.inProcessServerName");

        channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
    }

    @After
    public void teardown() throws Exception {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

}
