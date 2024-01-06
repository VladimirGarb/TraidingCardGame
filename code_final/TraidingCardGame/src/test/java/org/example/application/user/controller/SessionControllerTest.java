package org.example.application.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.application.user.model.Session;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class SessionControllerTest {

    @Mock
    UserRepository userRepository;
    @Mock
    SessionRepository sessionRepository;

    SessionController sessionController;

    @Test
    public void testCreateSuccessLogIn(){
        //Arrange
        sessionController = new SessionController(userRepository, sessionRepository);

        String username = "test";
        String password = "testpw";
        String token = "Basic test-mtcgToken";
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        Session session = new Session(username, token);

        Request request = new Request();
        request.setMethod("POST");
        request.setPath("/users");
        request.setContent("{\"Username\":\"test\", \"Password\":\"testpw\"}");
        request.setContentLength(request.getContent().length());

        when(userRepository.login(any())).thenReturn(user);

        ObjectMapper objectMapper = new ObjectMapper();

        //Act
        Response response = sessionController.handle(request);

        //Assert
        assertEquals(StatusCode.OK.code, response.getStatus());
        try {
            assertEquals(response.getContent(), objectMapper.writeValueAsString(session));
        } catch (JsonProcessingException e) {
            fail(e);
        }
    }

    @Test
    public void testCreateWrongCredentials(){
        //Arrange
        sessionController = new SessionController(userRepository, sessionRepository);

        String username = "test";
        String password = "testpw";
        String token = "Basic test-mtcgToken";
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        Session session = new Session(username, token);

        Request request = new Request();
        request.setMethod("POST");
        request.setPath("/users");
        request.setContent("{\"Username\":\"test\", \"Password\":\"testpw\"}");
        request.setContentLength(request.getContent().length());

        when(userRepository.login(any())).thenReturn(null);

        ObjectMapper objectMapper = new ObjectMapper();

        //Act
        Response response = sessionController.handle(request);

        //Assert
        assertEquals(StatusCode.UNAUTHORIZED.code, response.getStatus());
        assertEquals(response.getContent(), "\"Error: wrong credentials!\"");
    }

}