package org.example.application.user.repository;

import org.example.application.user.model.Session;
import org.example.application.user.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.example.server.DatabaseInitializer.conn;
public class SessionDataBaseRepository implements SessionRepository {

    @Override
    public List<Session> findAll() {
        String sessionFindAllSql = "SELECT * FROM sessions";
        List<Session> sessions = new ArrayList<>();
        try(
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sessionFindAllSql)
        ) {
            while (rs.next()) {
                Session session = new Session();
                session.setUsername(rs.getString("username"));
                session.setToken(rs.getString("token"));
                sessions.add(session);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return sessions;
    }

    @Override
    public Session findByToken(String token){

        String LookForSession = "SELECT * FROM sessions WHERE token = ?";

        try(PreparedStatement ps = conn.prepareStatement(LookForSession)) {
            ps.setString(1, token);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next())
                {
                    return new Session(rs.getString("username"), rs.getString("token"));
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
    public Session save(Session session){
        // check if the session already exists:
        Session LookForSession = findByToken(session.getToken());

        if (LookForSession == null){
            // insert new session
            String InsertSession = "INSERT INTO sessions (username, token) VALUES (?, ?)";

            try(PreparedStatement ps2 = conn.prepareStatement(InsertSession)) {
                ps2.setString(1, session.getUsername());
                ps2.setString(2, session.getToken());
                ps2.execute();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            return session;
        }
        else{
            return null;
        }
    }

    @Override
    public Session delete() {
        return null;
    }
}
