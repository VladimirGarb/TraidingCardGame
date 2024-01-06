package org.example;

import com.yourname.monstertradingcardgame.repository.DatabaseConnector;
import com.yourname.monstertradingcardgame.services.AuthenticationService;
import com.yourname.monstertradingcardgame.services.CardService;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    // Сделаем gson статическим, чтобы его можно было использовать в статическом контексте
    static Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(10001), 0);

        server.createContext("/users", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("POST".equals(exchange.getRequestMethod())) {
                    DatabaseConnector dbConnector = new DatabaseConnector();
                    AuthenticationService authService = new AuthenticationService(dbConnector);

                    // Считываем тело запроса
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

                    // Парсим JSON и создаем объект UserCredentials
                    UserCredentials credentials = gson.fromJson(body, UserCredentials.class);

                    boolean isRegistered = authService.registerUser(credentials.getUsername(), credentials.getPassword());

                    String responseText;
                    if (isRegistered) {
                        exchange.sendResponseHeaders(201, -1);
                        responseText = "User registered successfully";
                    } else {
                        exchange.sendResponseHeaders(400, -1);
                        responseText = "Failed to register user";
                    }

                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(responseText.getBytes());
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            }
        });

        server.createContext("/sessions", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("POST".equals(exchange.getRequestMethod())) {
                    DatabaseConnector dbConnector = new DatabaseConnector();
                    AuthenticationService authService = new AuthenticationService(dbConnector);

                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    UserCredentials credentials = gson.fromJson(body, UserCredentials.class);
                    boolean isLoggedIn = authService.loginUser(credentials.getUsername(), credentials.getPassword());

                    String responseText;
                    if (isLoggedIn) {
                        exchange.sendResponseHeaders(200, -1); // 200 OK
                        responseText = "User logged in successfully";
                    } else {
                        exchange.sendResponseHeaders(401, -1); // 401 Unauthorized
                        responseText = "Login failed";
                    }

                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(responseText.getBytes());
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
                }
            }
        });

        server.start();
        System.out.println("Server started on port 10001");
    }
}