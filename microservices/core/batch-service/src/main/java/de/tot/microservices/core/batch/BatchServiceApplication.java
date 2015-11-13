package de.tot.microservices.core.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan({"de.tot.microservices.core.batch", "se.callista.microservices.util"})
public class BatchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchServiceApplication.class, args);
    }
}
