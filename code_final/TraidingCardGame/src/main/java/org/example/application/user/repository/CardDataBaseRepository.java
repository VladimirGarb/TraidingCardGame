package org.example.application.user.repository;

import org.example.application.user.model.Card;
import org.example.application.user.model.CardType;
import org.example.application.user.model.Session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.example.server.DatabaseInitializer.conn;

public class CardDataBaseRepository implements CardRepository{

    @Override
    public List<Card> findAll() {
        String cardsFindAllSql = "SELECT * FROM cards";
        List<Card> cards = new ArrayList<>();
        try(
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(cardsFindAllSql)
        ) {
            while (rs.next()) {
                Card card = new Card();
                card.setId(rs.getString("id"));
                card.setName(rs.getString("cardname"));
                card.setDamage(rs.getInt("damage"));
                card.setCardType(CardType.valueOf(rs.getString("cardtype")));
                cards.add(card);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return cards;
    }

    @Override
    public Card findByID(String id) {

        String LookForCard = "SELECT * FROM cards WHERE id = ?";

        try(PreparedStatement ps = conn.prepareStatement(LookForCard)) {
            ps.setString(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next())
                {
                    return new Card(rs.getString("id"),rs.getString("cardname"), rs.getInt("damage"), rs.getString("cardtype"), rs.getString("username"));
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
    public Card save(Card card) {

        // insert new package
        String InsertCard = "INSERT INTO cards(id, cardname, damage, cardtype) VALUES (?, ?, ?, ?)";

        try(PreparedStatement ps1 = conn.prepareStatement(InsertCard)) {
            ps1.setString(1, card.getId());
            ps1.setString(2, card.getName());
            ps1.setInt(3, card.getDamage());
            ps1.setString(4, card.getCardType().name());

            ps1.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return card;
    }

    @Override
    public Card delete() {
        return null;
    }

    @Override
    public void appendUsername(String id, String username) {

        String InsertCard = "update cards set username = ? WHERE id = ?";

        try(PreparedStatement ps1 = conn.prepareStatement(InsertCard)) {
            ps1.setString(1, username);
            ps1.setString(2, id);

            ps1.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Card> allCardsUsername(String username) {
        String cardsFindAllByUsername = "SELECT * FROM cards WHERE username = ?";
        List<Card> cards = new ArrayList<>();

        try(PreparedStatement ps = conn.prepareStatement(cardsFindAllByUsername)) {
            ps.setString(1, username);
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next())
                {

                    Card card = new Card(rs.getString("id"), rs.getString("cardname"), rs.getInt("damage"), rs.getString("cardtype"), rs.getString("username"));
                    cards.add(card);

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return cards;
    }

    @Override
    public boolean checkCardToUsername(String username, String cardId) {

        String findCardById = "SELECT * FROM cards WHERE id = ?";
        Card card;

        try(PreparedStatement ps = conn.prepareStatement(findCardById)) {
            ps.setString(1, cardId);
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next())
                {
                    if(username.equals(rs.getString("username")))
                        return true;
                    else return false;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return false;
    }

}
