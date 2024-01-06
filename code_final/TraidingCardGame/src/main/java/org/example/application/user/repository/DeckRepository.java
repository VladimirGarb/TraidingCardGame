package org.example.application.user.repository;

import org.example.application.user.model.Deck;

public interface DeckRepository {

    Deck findByUsername(String username);

    Deck saveDeck(Deck deck);

    Deck updateDeck(Deck deck);

}
