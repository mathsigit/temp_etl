package com.island.tcbetl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
        excludeFilters =
        @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                value = {ApplicationReadyEvent.class, TcbetlApplication.class}))
public class TcbetlApplicationTests {

    public static void main(String[] args) {
        SpringApplication.run(TcbetlApplicationTests.class, args);
    }
}
