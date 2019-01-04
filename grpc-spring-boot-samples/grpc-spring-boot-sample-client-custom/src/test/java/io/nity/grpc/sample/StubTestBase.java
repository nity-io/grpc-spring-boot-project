package io.nity.grpc.sample;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class StubTestBase {

    private static final Logger log = LoggerFactory.getLogger(StubTestBase.class);

    @Autowired
    protected Environment environment;

    /**
     * This rule manages automatic graceful shutdown for the registered servers and channels at the end of test.
     */
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    /**
     * 创建server端实现
     *
     * @return
     */
    protected abstract BindableService makeServiceImpl();

    @Before
    public void setUp() throws Exception {
        int port = environment.getProperty("grpc.stub.port", Integer.class, 50051);
        BindableService serviceImpl = makeServiceImpl();
        Server server = ServerBuilder.forPort(port)
                .addService(serviceImpl)
                .build()
                .start();

        grpcCleanup.register(server);

        log.info("Test gRPC Server started, listening on " + port);
    }

}
