package org.example.application.user.model;

public class StatsVO {

    private final String name;
    private final int elo;
    private final int wins;
    private final int losses;

    public StatsVO(String name, int elo, int wins, int losses) {
        this.name = name;
        this.elo = elo;
        this.wins = wins;
        this.losses = losses;
    }

    public String getName() {
        return name;
    }

    public int getLosses() {
        return losses;
    }

    public int getWins() {
        return wins;
    }

    public int getElo() {
        return elo;
    }
}
