package org.example;

import org.example.application.user.MTCGApp;
import org.example.server.DatabaseInitializer;
import org.example.server.Server;

public class Main {

    public static void main(String[] args) {

        Server server = new Server(new MTCGApp());
        try {
            DatabaseInitializer.initialize();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}