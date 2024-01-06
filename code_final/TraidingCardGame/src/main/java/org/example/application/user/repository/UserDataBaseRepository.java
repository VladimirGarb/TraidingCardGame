package org.example.application.user.repository;

import org.example.application.user.model.StatsVO;
import org.example.application.user.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.example.server.DatabaseInitializer.conn;

public class UserDataBaseRepository implements UserRepository {

    //
    private final List<User> users;

    public UserDataBaseRepository() {
        this.users = new ArrayList<>();
    }

    @Override
    public List<User> findAll() {

        String userFindAllSql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try(
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(userFindAllSql)
        ) {
            while (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setCoins(rs.getInt("coins"));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users;
    }

    @Override
    public User findByUsername(String username) {

        String LookForUser = "SELECT * FROM users WHERE username = ?";

        try(PreparedStatement ps = conn.prepareStatement(LookForUser)) {
            ps.setString(1, username);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next())
                {
                    return new User(rs.getString("username"), rs.getString("password"), rs.getInt("coins"), rs.getString("name"), rs.getString("bio"), rs.getString("image"));
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
    public User save(User user)
    {

        // check if the user already exists:
        User LookForUser = findByUsername(user.getUsername());

        if (LookForUser == null){
            // insert new user
            String InsertUser = "INSERT INTO users (username, password, coins) VALUES (?, ?, ?)";

            try(PreparedStatement ps2 = conn.prepareStatement(InsertUser)) {
                ps2.setString(1, user.getUsername());
                ps2.setString(2, user.getPassword());
                ps2.setInt(3, 20);
                ps2.execute();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            return user;
        }
        else{
            return null;
        }


    }

    @Override
    public User delete(User user) {

        // check if the user really exists:
        User LookForUser = findByUsername(user.getUsername());

        if (LookForUser != null){
            // delete user
            String DeleteUser = "DELETE FROM users WHERE username = ? AND password = ?";

            try(PreparedStatement ps2 = conn.prepareStatement(DeleteUser)) {
                ps2.setString(1, user.getUsername());
                ps2.setString(2, user.getPassword());
                ps2.execute();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            return user;
        }
        else{
            return null;
        }
    }

    public User login(User user){

        String LookForUserWithPassword = "SELECT * FROM users WHERE username = ? AND password = ?";

        try(PreparedStatement ps = conn.prepareStatement(LookForUserWithPassword)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next())
                {
                    return new User(rs.getString("username"), rs.getString("password"), rs.getInt("coins"));
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
    public boolean payForPackage(String username) {

        User LookForUser = findByUsername(username);

        if (LookForUser != null) {
            //get the user by username:
            User user = findByUsername(username);
            if (user.getCoins() >= 5){
                // remove 5 coins
                String ChangeCoins = "UPDATE users SET coins = ? WHERE username = ? ";

                try (PreparedStatement ps2 = conn.prepareStatement(ChangeCoins)) {
                    ps2.setInt(1, user.getCoins() - 5);
                    ps2.setString(2, user.getUsername());
                    ps2.execute();

                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

            }
            else return false;
        }

        return false;
    }

    @Override
    public User updateUser(String username, String name, String bio, String image) {
        User LookForUser = findByUsername(username);

        if (LookForUser != null) {
            String ChangeUser = "UPDATE users SET name = ?, bio = ?, image = ? WHERE username = ?";

            try (PreparedStatement ps2 = conn.prepareStatement(ChangeUser)) {
                ps2.setString(1, name);
                ps2.setString(2, bio);
                ps2.setString(3, image);
                ps2.setString(4, username);
                ps2.execute();

                return findByUsername(username);

            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public List<StatsVO> getScoreboard() {

        String usersSortedByElo = "SELECT * FROM users ORDER BY elo ASC";
        List<StatsVO> scoreboard = new ArrayList<>();

        try(
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(usersSortedByElo)
        ) {
            while (rs.next()) {

                StatsVO user = new StatsVO(rs.getString("name"), rs.getInt("elo"), rs.getInt("wins"), rs.getInt("losses"));
                scoreboard.add(user);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return scoreboard;
    }

}
