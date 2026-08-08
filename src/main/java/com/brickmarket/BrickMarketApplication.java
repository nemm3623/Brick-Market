package com.brickmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BrickMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrickMarketApplication.class, args);
    }
}
