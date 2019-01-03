package io.nity.grpc.sample.grpc.interceptor;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 局部拦截器，需要在GrpcService配置
 */
public class RegionalGrpcInterceptor implements ServerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(GlobalGrpcInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        log.info("RegionalGrpcInterceptor begin...");
        log.info("grpc method:{}", call.getMethodDescriptor().getFullMethodName());
        log.info("grpc headers key:");

        for (String key : headers.keys()) {
            log.info("  " + key);
        }

        log.info("RegionalGrpcInterceptor end...");

        return next.startCall(call, headers);
    }
}
