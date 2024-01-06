package org.example.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private static final String DB_URL = "jdbc:postgresql://localhost/MTCGame";

    private static final String DB_USER = "mtcg_user";

    private static final String DB_PW = "test123";

    public static Connection conn;
    public static void initialize() throws SQLException{

        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PW);

        Statement stmt1 = conn.createStatement();
        stmt1.execute(
                """
                    CREATE TABLE IF NOT EXISTS users (
                        username VARCHAR(255) PRIMARY KEY,
                        password VARCHAR(255) NOT NULL,
                        coins int,
                        elo int
                    );
                    """
        );
        stmt1.close();

        Statement stmt3 = conn.createStatement();
        stmt3.execute(
                """
                    CREATE TABLE IF NOT EXISTS sessions (
                        username VARCHAR(255) PRIMARY KEY,
                        token VARCHAR(255) NOT NULL
                    );
                    """
        );
        stmt3.close();

        Statement stmt4 = conn.createStatement();
        stmt4.execute(
                """
                    CREATE TABLE IF NOT EXISTS cards (
                        id uuid PRIMARY KEY,
                        cardname VARCHAR(255) NOT NULL,
                        damage int,
                        cardType VARCHAR(255) NOT NULL,
                        username VARCHAR(255)
                    );
                    """
        );
        stmt4.close();

        Statement stmt5 = conn.createStatement();
        stmt5.execute(
                """
                    CREATE TABLE IF NOT EXISTS packages (
                        id serial,
                        card1 VARCHAR(255),
                        card2 VARCHAR(255),
                        card3 VARCHAR(255),
                        card4 VARCHAR(255),
                        card5 VARCHAR(255)
                    );
                    """
        );
        stmt5.close();

        Statement stmt6 = conn.createStatement();
        stmt6.execute(
                """
                    CREATE TABLE IF NOT EXISTS decks (
                        id serial,
                        username VARCHAR(255),
                        card1 VARCHAR(255),
                        card2 VARCHAR(255),
                        card3 VARCHAR(255),
                        card4 VARCHAR(255)
                    );
                    """
        );

        stmt6.close();

    }

}
