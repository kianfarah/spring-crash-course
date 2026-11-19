package com.example.crashcourse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

// @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan.
// Component scanning starts in THIS package, so all our classes live in com.example.crashcourse (or below).
@SpringBootApplication
@ConfigurationPropertiesScan // finds @ConfigurationProperties classes like AppProperties
public class CrashCourseApplication {

    public static void main(String[] args) {
        // Creates the ApplicationContext (the "container"), wires all beans, starts embedded Tomcat on :8080.
        SpringApplication.run(CrashCourseApplication.class, args);
    }
}
