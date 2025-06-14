package com.funchive.functionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EnableDiscoveryClient
public class FunchiveFunctionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FunchiveFunctionServiceApplication.class, args);
    }

}
