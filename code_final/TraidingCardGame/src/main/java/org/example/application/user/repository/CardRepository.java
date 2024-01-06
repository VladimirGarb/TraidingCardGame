package org.example.application.user.repository;

import org.example.application.user.model.Card;
import org.example.application.user.model.Package;

import java.util.List;

public interface CardRepository {

    List<Card> findAll();

    Card findByID(String id);

    Card save(Card card);

    Card delete();

    void appendUsername(String id, String username);

    List<Card> allCardsUsername(String token);

    boolean checkCardToUsername(String username, String cardId);
}
