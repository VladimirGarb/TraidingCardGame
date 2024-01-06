package org.example.application.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.ArrayList;

@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class User {

    private String username;
    private String password;
    private ArrayList<Card> stack;
    private int coins = 20;
    private String name;
    private String bio;
    private String image;

    int elo = 100;
    int wins;
    int losses;



    public User() {
    }

    public User(String username, String password, int coins) {
        this.username = username;
        this.password = password;
        this.coins = coins;
    }

    public User(String username, String password, int coins, String name, String bio, String image) {
        this.username = username;
        this.password = password;
        this.coins = coins;
        this.name = name;
        this.bio = bio;
        this.image = image;
    }

    public User(String username, String password, int coins, String name, String bio, String image, int elo, int wins, int losses) {
        this.username = username;
        this.password = password;
        this.coins = coins;
        this.name = name;
        this.bio = bio;
        this.image = image;
        this.elo = elo;
        this.wins = wins;
        this.losses = losses;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getElo() {
        return elo;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }
}
