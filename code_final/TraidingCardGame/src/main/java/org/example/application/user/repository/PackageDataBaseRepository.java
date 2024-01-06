package org.example.application.user.repository;

import org.example.application.user.model.Package;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.example.server.DatabaseInitializer.conn;

public class PackageDataBaseRepository implements PackageRepository{


    @Override
    public List<Package> findAll() {
        String packageFindAllSql = "SELECT * FROM packages";
        List<Package> packages = new ArrayList<>();
        try(
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(packageFindAllSql)
        )
        {
            while (rs.next()) {
                Package cardspackage = new Package();
                cardspackage.setCard1(rs.getString("card1"));
                cardspackage.setCard2(rs.getString("card2"));
                cardspackage.setCard3(rs.getString("card3"));
                cardspackage.setCard4(rs.getString("card4"));
                cardspackage.setCard5(rs.getString("card5"));
                packages.add(cardspackage);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return packages;
    }

    @Override
    public Package save(Package cardspackage) {

        // insert new package
        String InsertSession = "INSERT INTO packages(card1, card2, card3, card4, card5) VALUES (?, ?, ?, ?, ?)";

        try(PreparedStatement ps1 = conn.prepareStatement(InsertSession)) {
            ps1.setString(1, cardspackage.getCard1());
            ps1.setString(2, cardspackage.getCard2());
            ps1.setString(3, cardspackage.getCard3());
            ps1.setString(4, cardspackage.getCard4());
            ps1.setString(5, cardspackage.getCard5());

            ps1.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return cardspackage;
    }

    @Override
    public int deleteById(int id) {

        String deleteSession = "DELETE FROM packages WHERE id = ?";

        try(PreparedStatement ps1 = conn.prepareStatement(deleteSession)) {
            ps1.setInt(1, id);

            ps1.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return id;
    }

    @Override
    public Package getFirstAvailablePackage() {
        String findFirstPackage = "SELECT * FROM packages WHERE id = (SELECT MIN(id) FROM packages)";
        try(
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(findFirstPackage)
        )
        {
            while (rs.next()) {
                Package cardspackage = new Package();
                cardspackage.setId(rs.getInt("id"));
                cardspackage.setCard1(rs.getString("card1"));
                cardspackage.setCard2(rs.getString("card2"));
                cardspackage.setCard3(rs.getString("card3"));
                cardspackage.setCard4(rs.getString("card4"));
                cardspackage.setCard5(rs.getString("card5"));
                return cardspackage;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
