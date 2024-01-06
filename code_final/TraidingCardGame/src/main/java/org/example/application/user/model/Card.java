package org.example.application.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class Card {

    private String Id;
    private String name;
    private int damage;
    private CardType cardType;

    private String username;

    public Card(String id, String name, int damage, String cardType, String username) {

        this.Id = id;
        this.cardType = CardType.valueOf(cardType);
        this.name = name;
        this.damage = damage;
        this.username = username;
    }

    public Card() {

    }

    public String getUsername(){ return username; }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public CardType getCardType() {
        return cardType;
    }
}
