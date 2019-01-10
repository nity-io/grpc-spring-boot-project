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

package io.nity.grpc.sample.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

/**
 * 可以按平常的方式使用service
 */
@Slf4j
@Service
public class GreeterService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public String sayHello(String name) {
        log.info("GreeterService_sayHello name:{}", name);

        String sql = "insert into request_log (request_name,created_date) values (?,?)";
        Object args[] = {name, new Timestamp(System.currentTimeMillis())};
        int result = jdbcTemplate.update(sql, args);
        if (result > 0) {
            log.info("GreeterService_sayHello 数据保存成功 name:{}", name);
        }

        String message = "Hello " + name;

        return message;
    }

}