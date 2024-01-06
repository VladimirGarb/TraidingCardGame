package org.example.application.user.repository;

import org.example.application.user.model.Deck;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.example.server.DatabaseInitializer.conn;

public class DeckDataBaseRepository implements DeckRepository{

    @Override
    public Deck findByUsername(String username) {
        String LookForDeck = "SELECT * FROM decks WHERE username = ?";

        try(PreparedStatement ps = conn.prepareStatement(LookForDeck)) {
            ps.setString(1, username);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next())
                {
                    return new Deck(rs.getString("username"), rs.getString("card1"), rs.getString("card2"),rs.getString("card3"),rs.getString("card4"));
                }
                else
                {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Deck saveDeck(Deck deck) {
        // check if the user already has a deck configured:
        Deck LookForUser = findByUsername(deck.getUsername());

        if (LookForUser == null){
            // insert new deck
            String InsertDeck = "INSERT INTO decks (username, card1, card2, card3, card4 ) VALUES (?, ?, ?, ?, ?)";

            try(PreparedStatement ps2 = conn.prepareStatement(InsertDeck)) {
                ps2.setString(1, deck.getUsername());
                ps2.setString(2, deck.getCard1());
                ps2.setString(3, deck.getCard2());
                ps2.setString(4, deck.getCard3());
                ps2.setString(5, deck.getCard4());

                ps2.execute();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            return deck;
        }
        else{
            //update the entry
            return updateDeck(deck);
        }
    }

    @Override
    public Deck updateDeck(Deck deck) {
        // check if the user already has a deck configured:
        Deck LookForUser = findByUsername(deck.getUsername());

        // update the deck
        String InsertDeck = "UPDATE decks SET card1 = ?, card2 = ?, card3 = ?, card4 = ? WHERE username = ?";

        try(PreparedStatement ps2 = conn.prepareStatement(InsertDeck)) {
            ps2.setString(1, deck.getCard1());
            ps2.setString(2, deck.getCard2());
            ps2.setString(3, deck.getCard3());
            ps2.setString(4, deck.getCard4());
            ps2.setString(5, deck.getUsername());

            ps2.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return deck;
    }

}
