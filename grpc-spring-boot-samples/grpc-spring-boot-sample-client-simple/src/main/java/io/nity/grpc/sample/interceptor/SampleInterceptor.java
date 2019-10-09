/*
 * Copyright 2019 The nity.io gRPC Spring Boot Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nity.grpc.sample.interceptor;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 示例拦截器
 */
@Slf4j
public class SampleInterceptor implements ClientInterceptor {

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        //创建client

        log.info("创建client");

        ClientCall<ReqT, RespT> clientCall = next.newCall(method, callOptions);

        return new ForwardingClientCall<ReqT, RespT>() {
            @Override
            protected ClientCall<ReqT, RespT> delegate() {
                return clientCall;
            }

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                log.info("拦截器,在此可以对header参数进行修改");
                Metadata.Key<String> token = Metadata.Key.of("token", Metadata.ASCII_STRING_MARSHALLER);
                headers.put(token, "123456");
                super.start(responseListener, headers);
            }
        };
    }
}

