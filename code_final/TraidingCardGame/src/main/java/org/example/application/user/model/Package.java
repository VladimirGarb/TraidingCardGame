package org.example.application.user.model;

public class Package {

    private int id;
    private String card1, card2, card3, card4, card5;

    public Package(String cardID1, String cardID2, String cardID3, String cardID4, String cardID5) {
        this.card1 = cardID1;
        this.card2 = cardID2;
        this.card3 = cardID3;
        this.card4 = cardID4;
        this.card5 = cardID5;
    }

    public Package() {

    }

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public String getCard1() {
        return card1;
    }

    public String getCard2() {
        return card2;
    }

    public String getCard3() {
        return card3;
    }

    public String getCard4() {
        return card4;
    }

    public String getCard5() {
        return card5;
    }

    public void setCard1(String card1) {
        this.card1 = card1;
    }

    public void setCard2(String card2) {
        this.card2 = card2;
    }

    public void setCard3(String card3) {
        this.card3 = card3;
    }

    public void setCard4(String card4) {
        this.card4 = card4;
    }

    public void setCard5(String card5) {
        this.card5 = card5;
    }
}
