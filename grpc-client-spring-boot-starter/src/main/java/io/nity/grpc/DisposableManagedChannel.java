package io.nity.grpc;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.TimeUnit;

/**
 * 优雅关闭gRPC连接
 */
@Slf4j
public class DisposableManagedChannel extends Channel implements DisposableBean {

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
        log.info("Shutting down gRPC channel ...");
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

}
