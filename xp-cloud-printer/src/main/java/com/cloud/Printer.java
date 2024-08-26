package com.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication
@ComponentScans({@ComponentScan("com.takeoff.iot.modbus")})
public class Printer {
    public static void main(String[] args) {
        SpringApplication.run(Printer.class, args);
    }
}