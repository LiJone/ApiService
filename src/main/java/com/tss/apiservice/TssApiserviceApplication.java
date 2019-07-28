package com.tss.apiservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tss.apiservice.dao")
public class TssApiserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TssApiserviceApplication.class, args);
    }

}
