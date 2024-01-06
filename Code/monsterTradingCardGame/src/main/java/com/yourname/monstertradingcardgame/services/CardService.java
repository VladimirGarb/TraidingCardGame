package com.yourname.monstertradingcardgame.services;

import com.yourname.monstertradingcardgame.repository.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CardService {
    private final DatabaseConnector dbConnector;

    public CardService(DatabaseConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    // Метод для добавления новой карты
    public boolean addCard(String name, String type, int attack, int health) {
        String sql = "INSERT INTO cards (name, type, attack, health) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, type);
            pstmt.setInt(3, attack);
            pstmt.setInt(4, health);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // Метод для получения списка всех карт
    public List<Card> getAllCards() {
        List<Card> cards = new ArrayList<>();
        String sql = "SELECT * FROM cards";
        try (Connection conn = dbConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // Создание нового объекта карты и добавление его в список
                cards.add(new Card(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getInt("attack"),
                        rs.getInt("health")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return cards;
    }

    // Метод для удаления карты по id
    public boolean deleteCard(int cardId) {
        String sql = "DELETE FROM cards WHERE id = ?";
        try (Connection conn = dbConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cardId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // Метод для обновления карты по id
    public boolean updateCard(int cardId, String name, String type, int attack, int health) {
        String sql = "UPDATE cards SET name = ?, type = ?, attack = ?, health = ? WHERE id = ?";
        try (Connection conn = dbConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, type);
            pstmt.setInt(3, attack);
            pstmt.setInt(4, health);
            pstmt.setInt(5, cardId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // метод для поиска карт по имени
    public List<Card> searchCardsByName(String name) {
        List<Card> cards = new ArrayList<>();
        String sql = "SELECT * FROM cards WHERE name LIKE ?";
        try (Connection conn = dbConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + name + "%"); // Используем оператор LIKE для поиска
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                cards.add(new Card(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getInt("attack"),
                        rs.getInt("health")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return cards;
    }

    // Метод для фильтрации карт по типу
    public List<Card> filterCardsByType(String type) {
        List<Card> cards = new ArrayList<>();
        String sql = "SELECT * FROM cards WHERE type = ?";
        try (Connection conn = dbConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                cards.add(new Card(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getInt("attack"),
                        rs.getInt("health")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return cards;
    }
}

class Card {
    private int id;
    private String name;
    private String type;
    private int attack;
    private int health;

    // Конструктор, который принимает все атрибуты карты
    public Card(int id, String name, String type, int attack, int health) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.attack = attack;
        this.health = health;
    }

    // Геттеры и сеттеры для каждого поля
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    // Опционально: метод toString для удобного вывода информации о карте
    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", attack=" + attack +
                ", health=" + health +
                '}';
    }
}
