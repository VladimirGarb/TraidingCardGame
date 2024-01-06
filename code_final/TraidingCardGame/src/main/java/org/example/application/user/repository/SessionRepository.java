package org.example.application.user.repository;

import org.example.application.user.model.Session;
import org.example.application.user.model.User;

import java.util.List;

public interface SessionRepository {

    List<Session> findAll();

    Session findByToken(String token);

    Session save(Session session);

    Session delete();
}
