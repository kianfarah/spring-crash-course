package com.example.crashcourse.user;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

// Handles GET /api/users using the JDK's built-in HTTP server. Everything by hand:
// check the HTTP method, build the JSON string, set headers, write bytes, close the stream.
// Not handled at all: POST bodies (we'd have to PARSE JSON by hand), path variables like /api/users/1,
// input validation, error responses, escaping quotes in JSON, ...
public class UserHttpHandler implements HttpHandler {

    private final UserService userService;

    public UserHttpHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            exchange.close();
            return;
        }

        String json = userService.findAll().stream()
                .map(u -> "{\"id\":" + u.id() + ",\"username\":\"" + u.username() + "\",\"email\":\"" + u.email() + "\"}")
                .collect(Collectors.joining(",", "[", "]"));

        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, body.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
        }
    }
}
