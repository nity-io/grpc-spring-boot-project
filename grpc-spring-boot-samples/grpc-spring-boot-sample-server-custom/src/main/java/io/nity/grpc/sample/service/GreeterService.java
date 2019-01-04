package io.nity.grpc.sample.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

/**
 * 可以按平常的方式使用service
 */
@Service
public class GreeterService {
    private static final Logger log = LoggerFactory.getLogger(GreeterService.class);

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