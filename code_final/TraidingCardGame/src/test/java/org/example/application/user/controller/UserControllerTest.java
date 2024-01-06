package org.example.application.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.application.user.model.Session;
import org.example.application.user.model.User;
import org.example.application.user.repository.SessionRepository;
import org.example.application.user.repository.UserRepository;
import org.example.application.user.controller.UserController;
import org.example.server.dto.Request;
import org.example.server.dto.Response;
import org.example.server.http.StatusCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.engine.support.discovery.SelectorResolver;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    UserRepository userRepository;
    @Mock
    SessionRepository sessionRepository;
    UserController userController;

    @Test
    public void testReadUserPass(){
        //Arrange
        userController = new UserController(userRepository, sessionRepository);

        String username = "test";
        String token = "Basic test-mtcgToken";
        User user = new User(username, "test", 0);
        Session session = new Session(username, token);

        when(sessionRepository.findByToken(token)).thenReturn(session);
        when(userRepository.findByUsername(username)).thenReturn(user);

        Request request = new Request();
        request.setMethod("GET");
        request.setPath("/users/" +username);
        request.setAuthorization(token);
        request.setContentLength(0);
        request.setContent("");

        ObjectMapper objectMapper = new ObjectMapper();

        //Act
        Response response = userController.handle(request);

        //Assert
        assertEquals(StatusCode.OK.code, response.getStatus());
        try {
            assertEquals(response.getContent(), objectMapper.writeValueAsString(user));
        } catch (JsonProcessingException e) {
            fail(e);
        }
    }

    @Test
    public void testReadUserInvalidToken(){
        //Arrange
        userController = new UserController(userRepository, sessionRepository);

        String username = "test";
        String token = "Basic test-mtcgToken";
        User user = new User(username, "test", 0);
        Session session = new Session(username, token);

        when(sessionRepository.findByToken(token)).thenReturn(session);

        Request request = new Request();
        request.setMethod("GET");
        request.setPath("/users/notTest");
        request.setAuthorization(token);
        request.setContentLength(0);
        request.setContent("");

        //Act
        Response response = userController.handle(request);

        //Assert
        assertEquals(StatusCode.UNAUTHORIZED.code, response.getStatus());
        assertEquals(response.getContent(), "Access token is missing or invalid");
    }

    @Test
    public void testCreateSuccessfully(){
        //Arrange
        userController = new UserController(userRepository, sessionRepository);

        String username = "test";
        String password = "testpw";
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        Request request = new Request();
        request.setMethod("POST");
        request.setPath("/users");
        request.setContent("{\"Username\":\"test\", \"Password\":\"testpw\"}");
        request.setContentLength(request.getContent().length());

        when(userRepository.save(any())).thenReturn(user);

        ObjectMapper objectMapper = new ObjectMapper();

        //Act
        Response response = userController.handle(request);

        //Assert
        assertEquals(StatusCode.CREATED.code, response.getStatus());
        try{
            assertEquals(response.getContent(), objectMapper.writeValueAsString(user));
        } catch (JsonProcessingException e) {
            fail(e);
        }

    }

    @Test
    public void testCreateUserAlreadyExists(){
        //Arrange
        userController = new UserController(userRepository, sessionRepository);

        String username = "test";
        String password = "testpw";
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        lenient().when(userRepository.save(user)).thenReturn(null);

        Request request = new Request();
        request.setMethod("POST");
        request.setPath("/users");
        request.setContent("{\"Username\":\"test\", \"Password\":\"testpw\"}");
        request.setContentLength(request.getContent().length());

        //Act
        Response response = userController.handle(request);

        //Assert
        assertEquals(StatusCode.CONFLICT.code, response.getStatus());
        assertEquals(response.getContent(), "\"User with same username already registered\"");
    }

    @Test
    public void testChangeData(){
        //Arrange
        userController = new UserController(userRepository, sessionRepository);

        String username = "test";
        String password = "testpw";
        String token = "Basic test-mtcgToken";
        User user = new User(username, password, 20, "Test", "me playin...", ":-)");
        Session session = new Session(username, token);

        Request request = new Request();
        request.setMethod("PUT");
        request.setPath("/users/" +username);
        request.setContent("{\"Name\": \"Test\",  \"Bio\": \"me playin...\", \"Image\": \":-)\"}");
        request.setContentLength(request.getContent().length());

        when(sessionRepository.findByToken(request.getAuthorization())).thenReturn(session);
        when(userRepository.updateUser(username, user.getName(), user.getBio(), user.getImage())).thenReturn(user);

        ObjectMapper objectMapper = new ObjectMapper();

        //Act
        Response response = userController.handle(request);

        //Assert
        assertEquals(StatusCode.OK.code, response.getStatus());
        try {
            assertEquals(response.getContent(), objectMapper.writeValueAsString(user));
        } catch (JsonProcessingException e) {
            fail(e);
        }

    }

    @Test
    public void testChangeDataInvalidToken(){ //invalid token
        //Arrange
        userController = new UserController(userRepository, sessionRepository);

        String username = "test";
        String password = "testpw";
        String token = "Basic test-mtcgToken";
        User user = new User(username, password, 20, "Test", "me playin...", ":-)");
        Session session = new Session(username, token);

        Request request = new Request();
        request.setMethod("PUT");
        request.setPath("/users/notTest");
        request.setContent("{\"Name\": \"Test\",  \"Bio\": \"me playin...\", \"Image\": \":-)\"}");
        request.setContentLength(request.getContent().length());

        when(sessionRepository.findByToken(request.getAuthorization())).thenReturn(session);

        //Act
        Response response = userController.handle(request);

        //Assert
        assertEquals(StatusCode.UNAUTHORIZED.code, response.getStatus());
        assertEquals(response.getContent(), "Access token is missing or invalid");

    }
}