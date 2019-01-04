package io.nity.grpc.sample.grpc.interceptor;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.nity.grpc.GrpcGlobalInterceptor;

/**
 * 全局拦截器，对所有gRPC service生效
 */
@GrpcGlobalInterceptor
public class GlobalGrpcInterceptor implements ServerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(GlobalGrpcInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        log.info("GlobalGrpcInterceptor begin...");
        log.info("grpc method:{}", call.getMethodDescriptor().getFullMethodName());
        log.info("grpc headers key:");

        for (String key : headers.keys()) {
            log.info("  " + key);
        }

        log.info("GlobalGrpcInterceptor end...");

        return next.startCall(call, headers);
    }
}
