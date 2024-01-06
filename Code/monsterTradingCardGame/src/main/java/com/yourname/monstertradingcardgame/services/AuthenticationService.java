package com.yourname.monstertradingcardgame.services;

import com.yourname.monstertradingcardgame.repository.DatabaseConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class AuthenticationService {
    private final DatabaseConnector dbConnector;

    public AuthenticationService(DatabaseConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    // Метод для валидации пароля
    private boolean isPasswordValid(String password) {
        // Проверка на минимальную длину
        if (password.length() < 4) {  // Уменьшена длина для примера
            System.out.println("Пароль должен содержать минимум 4 символа.");
            return false;
        }
        return true; // Пароль валиден
    }

    // Метод для регистрации нового пользователя
    public boolean registerUser(String username, String password) {
        // Валидация имени пользователя и пароля
        if (!isPasswordValid(password)) {
            return false;  // Прекращаем регистрацию, если валидация не пройдена
        }

        // Проверка, существует ли уже такой пользователь
        if (isUserExists(username)) {
            System.out.println("Пользователь с таким именем уже существует: " + username);
            return false;  // Прекращаем регистрацию, если пользователь существует
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Определяем роль пользователя
        String role = "user"; // по умолчанию все пользователи имеют роль "user"
        if ("admin".equals(username)) {
            role = "admin"; // если имя пользователя "admin", присваиваем роль "admin"
        }

        // Добавляем пользователя в базу данных с ролью
        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";

        try (Connection conn = dbConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean isUserExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = dbConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;  // Если пользователь существует, вернуть true
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;  // Если пользователь не найден, вернуть false
    }

    // Метод для входа пользователя
    public boolean loginUser(String username, String password) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";

        try (Connection conn = dbConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                return BCrypt.checkpw(password, storedHash);
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
        return false;
    }
}

