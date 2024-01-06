package org.example.application.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.application.user.model.Deck;
import org.example.application.user.model.Session;
import org.example.application.user.model.User;
import org.example.application.user.repository.CardRepository;
import org.example.application.user.repository.DeckRepository;
import org.example.application.user.repository.SessionRepository;
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
class DeckControllerTest {

    @Mock
    SessionRepository sessionRepository;
    @Mock
    DeckRepository deckRepository;
    @Mock
    CardRepository cardRepository;

    DeckController deckController;

    @Test
    public void testReadDeck(){

        //Arrange
        deckController = new DeckController(sessionRepository, deckRepository, cardRepository);

        String username = "test";
        String token = "Basic test-mtcgToken";
        User user = new User(username, "test", 0);
        Deck deck = new Deck(username, "id1", "id2", "id3", "id4");
        Session session = new Session(username, token);

        Request request = new Request();
        request.setMethod("GET");
        request.setPath("/deck");
        request.setAuthorization(token);
        request.setContentLength(0);
        request.setContent("");

        when(sessionRepository.findByToken(token)).thenReturn(session);
        when(deckRepository.findByUsername(username)).thenReturn(deck);

        ObjectMapper objectMapper = new ObjectMapper();

        //Act
        Response response = deckController.handle(request);

        //Assert
        assertEquals(StatusCode.OK.code, response.getStatus());
        try {
            assertEquals(response.getContent(), objectMapper.writeValueAsString(deck));
        } catch (JsonProcessingException e) {
            fail(e);
        }
    }

    @Test
    public void testReadDeckInvalidToken(){

        //Arrange
        deckController = new DeckController(sessionRepository, deckRepository, cardRepository);

        String username = "test";
        String token = "Basic test-mtcgToken";
        User user = new User(username, "test", 0);
        Deck deck = new Deck(username, "id1", "id2", "id3", "id4");
        Session session = new Session(username, token);

        Request request = new Request();
        request.setMethod("GET");
        request.setPath("/deck");
        request.setContentLength(0);
        request.setContent("");

        ObjectMapper objectMapper = new ObjectMapper();

        //Act
        Response response = deckController.handle(request);

        //Assert
        assertEquals(StatusCode.UNAUTHORIZED.code, response.getStatus());
        assertEquals(response.getContent(), "Access token is missing or invalid");

    }

    @Test
    public void testReadDeckNotConfigured(){

        //Arrange
        deckController = new DeckController(sessionRepository, deckRepository, cardRepository);

        String username = "test";
        String token = "Basic test-mtcgToken";
        User user = new User(username, "test", 0);
        Deck deck = new Deck(username, "id1", "id2", "id3", "id4");
        Session session = new Session(username, token);

        Request request = new Request();
        request.setMethod("GET");
        request.setPath("/deck");
        request.setAuthorization(token);
        request.setContentLength(0);
        request.setContent("");

        when(sessionRepository.findByToken(token)).thenReturn(session);
        when(deckRepository.findByUsername(username)).thenReturn(null);

        ObjectMapper objectMapper = new ObjectMapper();

        //Act
        Response response = deckController.handle(request);

        //Assert
        assertEquals(StatusCode.NO_CONTENT.code, response.getStatus());
        assertEquals(response.getContent(), "\"Deck not configured\"");

    }
}