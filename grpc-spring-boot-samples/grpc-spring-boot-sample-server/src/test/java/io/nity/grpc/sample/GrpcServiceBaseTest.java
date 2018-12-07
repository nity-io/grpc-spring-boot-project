package io.nity.grpc.sample;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.nity.grpc.context.LocalRunningGrpcPort;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SampleServerApp.class)
public abstract class GrpcServiceBaseTest {

    @Autowired
    protected Environment environment;

    @LocalRunningGrpcPort
    private int port;

    protected ManagedChannel channel;

    @Before
    public void setUp() throws Exception {
        String serverName = environment.getProperty("grpc.server.inProcessServerName");

        if (StringUtils.isEmpty(serverName)) {
            channel = ManagedChannelBuilder.forAddress("localhost", port)
                    // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                    // needing certificates.
                    .usePlaintext()
                    .build();
        } else {
            channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
        }
    }

    @After
    public void teardown() throws Exception {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

}
