package com.example.crashcourse;

import com.example.crashcourse.notification.WelcomeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

// @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan.
// Component scanning starts in THIS package, so all our classes live in com.example.crashcourse (or below).
@SpringBootApplication
@ConfigurationPropertiesScan // finds @ConfigurationProperties classes like AppProperties
public class CrashCourseApplication {

    public static void main(String[] args) {
        // Creates the ApplicationContext (the "container"), wires all beans, starts embedded Tomcat on :8080.
        SpringApplication.run(CrashCourseApplication.class, args);

        // WITHOUT Spring you would wire everything by hand, in the right order:
        //   NotificationService email = new EmailNotificationService();
        //   WelcomeService welcome = new WelcomeService(email, Clock.systemDefaultZone());
        // Fine for 2 classes, painful for 200.
    }

    // A CommandLineRunner bean runs once after startup. Its parameters are injected like any other bean.
    @Bean
    CommandLineRunner demo(WelcomeService welcomeService, ApplicationContext context) {
        return args -> {
            welcomeService.welcome("ada@example.com");

            System.out.println("Our own beans in the container:");
            Arrays.stream(context.getBeanDefinitionNames())
                    .filter(name -> context.getType(name) != null
                            && context.getType(name).getPackageName().startsWith("com.example"))
                    .forEach(name -> System.out.println("  - " + name + " -> " + context.getType(name).getSimpleName()));
        };
    }
}
