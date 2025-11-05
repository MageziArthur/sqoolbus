package com.sqool.sqoolbus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class SqoolbusApplication {

    public static void main(String[] args) {
        SpringApplication.run(SqoolbusApplication.class, args);
    }
}