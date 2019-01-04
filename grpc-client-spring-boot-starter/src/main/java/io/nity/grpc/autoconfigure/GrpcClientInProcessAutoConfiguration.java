package io.nity.grpc.autoconfigure;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.nity.grpc.DisposableManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

@Slf4j
@AutoConfigureOrder
public class GrpcClientInProcessAutoConfiguration {

    @Autowired
    private GrpcClientProperties grpcProperties;

    @Bean
    @ConditionalOnProperty(value = "grpc.stub.model", havingValue = GrpcClientProperties.SERVER_MODEL_IN_PROCESS)
    public DisposableManagedChannel getChannel() {
        GrpcClientProperties.StubProperties stub = grpcProperties.getStub();

        ManagedChannel channel;

        String inProcessServerName = stub.getInProcessServerName();

        if (!StringUtils.hasText(inProcessServerName)) {
            log.error("please config required property [inProcessServerName] for InProcess model");
            throw new RuntimeException("Failed to create inProcessChannel");
        }

        log.warn("will create InProcessChannel. Please only use in testing");
        channel = InProcessChannelBuilder.forName(inProcessServerName).directExecutor().build();

        DisposableManagedChannel disposableManagedChannel = new DisposableManagedChannel(channel);
        return disposableManagedChannel;
    }

}
