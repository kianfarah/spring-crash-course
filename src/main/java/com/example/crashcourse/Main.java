package com.example.crashcourse;

import com.example.crashcourse.notification.EmailNotificationService;
import com.example.crashcourse.notification.NotificationService;
import com.example.crashcourse.notification.WelcomeService;
import com.example.crashcourse.user.UserHttpHandler;
import com.example.crashcourse.user.UserRepository;
import com.example.crashcourse.user.UserService;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.time.Clock;
import java.util.Properties;

// Plain Java, no Spring. We are the "container": we create every object ourselves and plug them together.
public class Main {

    public static void main(String[] args) throws IOException {
        // 1) Configuration: read the file, parse it, convert types, handle "missing" ourselves.
        Properties config = new Properties();
        try (InputStream in = Main.class.getResourceAsStream("/app.properties")) {
            config.load(in);
        }
        String welcomeMessage = config.getProperty("app.welcome-message", "Welcome!");
        int port = Integer.parseInt(config.getProperty("server.port", "8080"));

        // 2) Wiring: create every object with "new", in exactly the right order
        //    (each one needs the previous ones in its constructor).
        Clock clock = Clock.systemDefaultZone();
        NotificationService notificationService = new EmailNotificationService(); // want SMS? Edit this line.
        WelcomeService welcomeService = new WelcomeService(notificationService, clock, welcomeMessage);
        UserRepository userRepository = new UserRepository();
        UserService userService = new UserService(userRepository, welcomeService);
        UserHttpHandler userHttpHandler = new UserHttpHandler(userService);
        // ...a real app has hundreds of these lines. Add a constructor parameter anywhere -> fix it here.

        // 3) Some demo data.
        userService.create("ada", "ada@example.com");
        userService.create("grace", "grace@example.com");

        // 4) Web server: create it, register every URL by hand, start it.
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/users", userHttpHandler);
        server.start();
        System.out.println("Listening on http://localhost:" + port + "/api/users");
    }
}
