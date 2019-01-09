package io.nity.grpc;

import io.grpc.ServerBuilder;

public interface GrpcServerBuilderConfigurer {
    void configure(ServerBuilder<?> serverBuilder);
}
