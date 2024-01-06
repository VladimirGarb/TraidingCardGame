package org.example.application.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.application.user.model.Package;
import org.example.application.user.model.Session;
import org.example.application.user.repository.*;
import org.example.server.dto.Request;
import org.example.server.dto.Response;
import org.example.server.http.StatusCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PackageAcquireControllerTest {

    @Mock
    SessionRepository sessionRepository;
    @Mock
    PackageRepository packageRepository;
    @Mock
    CardRepository cardRepository;
    @Mock
    UserRepository userRepository;

    PackageAcquireController packageAcquireController;

    @Test
    public void testCreate(){

        //Arrange
        packageAcquireController = new PackageAcquireController(sessionRepository, packageRepository, cardRepository, userRepository);
        Package package1 = new Package("id1", "id2", "id3", "id4", "id5");
        String token = "Basic test-mtcgToken";
        String username = "test";
        Session session = new Session(username, token);

        Request request = new Request();
        request.setMethod("POST");
        request.setPath("/transactions/packages");
        request.setAuthorization(token);
        request.setContentLength(0);
        request.setContent("");

        when(sessionRepository.findByToken(token)).thenReturn(session);
        when(packageRepository.getFirstAvailablePackage()).thenReturn(package1);
        when(userRepository.payForPackage(session.getUsername())).thenReturn(true);

        ObjectMapper objectMapper = new ObjectMapper();

        //Act
        Response response = packageAcquireController.handle(request);

        //Assert
        assertEquals(StatusCode.OK.code, response.getStatus());
        try {
            assertEquals(response.getContent(), objectMapper.writeValueAsString(package1));
        } catch (JsonProcessingException e) {
            fail(e);
        }

    }

    @Test
    public void testCreateNotEnoughMoney(){

        //Arrange
        packageAcquireController = new PackageAcquireController(sessionRepository, packageRepository, cardRepository, userRepository);
        Package package1 = new Package("id1", "id2", "id3", "id4", "id5");
        String token = "Basic test-mtcgToken";
        String username = "test";
        Session session = new Session(username, token);

        Request request = new Request();
        request.setMethod("POST");
        request.setPath("/transactions/packages");
        request.setAuthorization(token);
        request.setContentLength(0);
        request.setContent("");

        when(sessionRepository.findByToken(token)).thenReturn(session);
        when(packageRepository.getFirstAvailablePackage()).thenReturn(package1);
        when(userRepository.payForPackage(session.getUsername())).thenReturn(false);

        ObjectMapper objectMapper = new ObjectMapper();

        //Act
        Response response = packageAcquireController.handle(request);

        //Assert
        assertEquals(StatusCode.FORBIDDEN.code, response.getStatus());
        assertEquals(response.getContent(), "Error: not enough money");


    }

    @Test
    public void testCreateNoAvailablePackage(){

        //Arrange
        packageAcquireController = new PackageAcquireController(sessionRepository, packageRepository, cardRepository, userRepository);
        Package package1 = new Package("id1", "id2", "id3", "id4", "id5");
        String token = "Basic test-mtcgToken";
        String username = "test";
        Session session = new Session(username, token);

        Request request = new Request();
        request.setMethod("POST");
        request.setPath("/transactions/packages");
        request.setAuthorization(token);
        request.setContentLength(0);
        request.setContent("");

        when(sessionRepository.findByToken(token)).thenReturn(session);
        when(packageRepository.getFirstAvailablePackage()).thenReturn(null);


        ObjectMapper objectMapper = new ObjectMapper();

        //Act
        Response response = packageAcquireController.handle(request);

        //Assert
        assertEquals(StatusCode.NOT_FOUND.code, response.getStatus());
        assertEquals(response.getContent(), "Error: no package available");

    }

    @Test
    public void testCreateInvalidToken(){

        //Arrange
        packageAcquireController = new PackageAcquireController(sessionRepository, packageRepository, cardRepository, userRepository);
        Package package1 = new Package("id1", "id2", "id3", "id4", "id5");
        String token = "Basic test-mtcgToken";
        String username = "test";
        Session session = new Session(username, token);

        Request request = new Request();
        request.setMethod("POST");
        request.setPath("/transactions/packages");
        request.setContentLength(0);
        request.setContent("");

        //Act
        Response response = packageAcquireController.handle(request);

        //Assert
        assertEquals(StatusCode.UNAUTHORIZED.code, response.getStatus());
        assertEquals(response.getContent(), "Access token is missing or invalid");

    }

}