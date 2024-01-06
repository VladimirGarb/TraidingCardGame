package org.example.application.user.model;

public class Deck {

    int id;
    String username;
    String card1, card2, card3, card4;

    public Deck(String username, String card1, String card2, String card3, String card4) {
        this.username = username;
        this.card1 = card1;
        this.card2 = card2;
        this.card3 = card3;
        this.card4 = card4;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCard1() {
        return card1;
    }

    public void setCard1(String card1) {
        this.card1 = card1;
    }

    public String getCard2() {
        return card2;
    }

    public void setCard2(String card2) {
        this.card2 = card2;
    }

    public String getCard3() {
        return card3;
    }

    public void setCard3(String card3) {
        this.card3 = card3;
    }

    public String getCard4() {
        return card4;
    }

    public void setCard4(String card4) {
        this.card4 = card4;
    }
}
