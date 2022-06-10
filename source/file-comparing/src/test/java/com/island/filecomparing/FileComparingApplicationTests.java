package com.island.filecomparing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootTest
@ComponentScan(
        excludeFilters =
        @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                value = {ApplicationReadyEvent.class, FileComparingApplication.class}))
class FileComparingApplicationTests {

    public static void main(String[] args) {
        SpringApplication.run(FileComparingApplicationTests.class, args);
    }

}
