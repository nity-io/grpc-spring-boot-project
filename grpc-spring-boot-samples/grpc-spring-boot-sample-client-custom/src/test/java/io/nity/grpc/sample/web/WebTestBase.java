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

package io.nity.grpc.sample.web;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import io.nity.grpc.client.config.GrpcClientProperties;
import io.nity.grpc.client.config.GrpcClientPropertiesMap;
import io.nity.grpc.sample.SampleClientCustomApp;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SampleClientCustomApp.class, webEnvironment=SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableConfigurationProperties(GrpcClientPropertiesMap.class)
public abstract class WebTestBase {

    @Autowired
    protected GrpcClientPropertiesMap clientPropertiesMap;
    @Autowired
    protected Environment environment;
    @Autowired
    protected RestTemplate restTemplate;

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
        GrpcClientProperties clientProperties = clientPropertiesMap.getClient("default");

        int port = clientProperties.getPort();

        BindableService serviceImpl = makeServiceImpl();
        Server server = ServerBuilder.forPort(port)
                .addService(serviceImpl)
                .build()
                .start();

        grpcCleanup.register(server);

        log.info("Test gRPC Server started, listening on " + port);
    }

    @After
    public void teardown() throws Exception {

    }
}
