package org.example.application.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.application.user.model.Session;
import org.example.application.user.model.StatsVO;
import org.example.application.user.model.User;
import org.example.application.user.repository.SessionRepository;
import org.example.application.user.repository.UserRepository;
import org.example.server.dto.Request;
import org.example.server.dto.Response;
import org.example.server.http.StatusCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class StatsControllerTest {

    @Mock
    UserRepository userRepository;
    @Mock
    SessionRepository sessionRepository;

    StatsController statsController;

    @Test
    public void testReadStats(){
        //Arrange
        statsController = new StatsController(userRepository, sessionRepository);

        String username = "test";
        String token = "Basic test-mtcgToken";
        User user = new User(username, "testpw", 20, "test", "", "", 1, 2, 1);
        StatsVO stats = new StatsVO("test",1,2,1);
        Session session = new Session(username, token);

        when(sessionRepository.findByToken(token)).thenReturn(session);
        when(userRepository.findByUsername(username)).thenReturn(user);

        Request request = new Request();
        request.setMethod("GET");
        request.setPath("/stats");
        request.setAuthorization(token);
        request.setContentLength(0);
        request.setContent("");

        ObjectMapper objectMapper = new ObjectMapper();

        //Act
        Response response = statsController.handle(request);

        //Assert
        assertEquals(StatusCode.OK.code, response.getStatus());
        try {
            assertEquals(response.getContent(), objectMapper.writeValueAsString(stats));
        } catch (JsonProcessingException e) {
            fail(e);
        }
    }

    @Test
    public void testReadStatsInvalidToken(){
        //Arrange
        statsController = new StatsController(userRepository, sessionRepository);

        String username = "test";
        String token = "Basic test-mtcgToken";
        User user = new User(username, "testpw", 20, "test", "", "", 1, 2, 1);
        StatsVO stats = new StatsVO("test",1,2,1);
        Session session = new Session(username, token);

        Request request = new Request();
        request.setMethod("GET");
        request.setPath("/stats");
        request.setContentLength(0);
        request.setContent("");

        when(sessionRepository.findByToken(request.getAuthorization())).thenReturn(session);

        //Act
        Response response = statsController.handle(request);

        //Assert
        assertEquals(StatusCode.UNAUTHORIZED.code, response.getStatus());
        assertEquals(response.getContent(), "Access token is missing or invalid");
    }
}