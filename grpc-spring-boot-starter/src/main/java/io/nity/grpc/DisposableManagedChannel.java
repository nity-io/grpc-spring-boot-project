package io.nity.grpc;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.TimeUnit;

/**
 * 优雅关闭gRPC连接
 */
public class DisposableManagedChannel extends Channel implements DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(DisposableManagedChannel.class);

    private ManagedChannel channel;

    public DisposableManagedChannel(ManagedChannel channel) {
        this.channel = channel;
    }

    @Override
    public <RequestT, ResponseT> ClientCall<RequestT, ResponseT> newCall(MethodDescriptor<RequestT, ResponseT> methodDescriptor, CallOptions callOptions) {
        return channel.newCall(methodDescriptor, callOptions);
    }

    @Override
    public String authority() {
        return channel.authority();
    }

    @Override
    public void destroy() throws Exception {
        logger.info("Shutting down gRPC channel ...");
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
