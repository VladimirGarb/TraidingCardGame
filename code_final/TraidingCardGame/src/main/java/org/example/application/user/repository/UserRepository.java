package org.example.application.user.repository;

import org.example.application.user.model.StatsVO;
import org.example.application.user.model.User;

import java.util.List;

public interface UserRepository {

    List<User> findAll();

    User findByUsername(String username);

    User save(User user);

    User delete(User user);

    User login(User user);

    boolean payForPackage(String username);

    User updateUser(String username, String name, String bio, String image);

    List<StatsVO> getScoreboard();

}
