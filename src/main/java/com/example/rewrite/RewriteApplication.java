package com.example.rewrite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.SystemMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EntityScan
@EnableAsync

@SpringBootApplication(exclude = {SystemMetricsAutoConfiguration.class})
public class RewriteApplication {

    public static void main(String[] args) {
        SpringApplication.run(RewriteApplication.class, args);
    }

}
