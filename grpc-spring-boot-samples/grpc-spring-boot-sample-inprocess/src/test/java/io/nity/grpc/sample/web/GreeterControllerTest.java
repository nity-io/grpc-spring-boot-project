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

import io.grpc.examples.helloworld.GreeterGrpc;
import io.nity.grpc.sample.SampleInProcessApp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SampleInProcessApp.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class GreeterControllerTest {

    @Autowired
    protected RestTemplate restTemplate;

    /**
     * 为了mock final的gRPC stub，需要mockito-inline 2.*版本
     */
    @MockBean
    private GreeterGrpc.GreeterBlockingStub greeterBlockingStub;

    @Test
    public void testSayHello() throws Exception {
        String url = "http://localhost:8080/greet";

        String response = restTemplate.getForEntity(url, String.class).getBody();

        Assert.assertEquals("Hello World", response);
    }

}
