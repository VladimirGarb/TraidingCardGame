package org.example.application.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.example.application.user.model.Card;
import org.example.application.user.model.Deck;
import org.example.application.user.model.Package;
import org.example.application.user.model.Session;
import org.example.application.user.repository.CardRepository;
import org.example.application.user.repository.DeckRepository;
import org.example.application.user.repository.SessionRepository;
import org.example.server.dto.Request;
import org.example.server.dto.Response;
import org.example.server.http.ContentType;
import org.example.server.http.Method;
import org.example.server.http.StatusCode;

import java.util.Objects;

public class DeckController {

    SessionRepository sessionRepository;
    DeckRepository deckRepository;
    CardRepository cardRepository;
    public DeckController(SessionRepository sessionRepository, DeckRepository deckRepository, CardRepository cardRepository) {
        this.deckRepository = deckRepository;
        this.sessionRepository = sessionRepository;
        this.cardRepository = cardRepository;
    }

    public Response handle(Request request) {

        if (request.getMethod().equals(Method.PUT.method)) {
            if(request.getAuthorization() != null && sessionRepository.findByToken(request.getAuthorization()) != null)
                return update(request);
            else{

                Response response = new Response();
                response.setStatusCode(StatusCode.UNAUTHORIZED);
                response.setContentType(ContentType.TEXT_PLAIN);
                response.setContent("Access token is missing or invalid");

                return response;
            }
        }

        if (request.getMethod().equals(Method.GET.method)) {
            if(request.getAuthorization() != null && sessionRepository.findByToken(request.getAuthorization()) != null)
                return readDeck(sessionRepository.findByToken(request.getAuthorization()).getUsername());
            else{

                Response response = new Response();
                response.setStatusCode(StatusCode.UNAUTHORIZED);
                response.setContentType(ContentType.TEXT_PLAIN);
                response.setContent("Access token is missing or invalid");

                return response;
            }

        }

        Response response = new Response();
        response.setStatusCode(StatusCode.METHODE_NOT_ALLOWED);
        response.setContentType(ContentType.TEXT_PLAIN);
        response.setContent(StatusCode.METHODE_NOT_ALLOWED.message);

        return response;
    }

    private Response readDeck(String username) {
        ObjectMapper objectMapper = new ObjectMapper();

        Response response = new Response();
        response.setContentType(ContentType.APPLICATION_JSON);

        String content;
        try {
            if(deckRepository.findByUsername(username) != null) {
                response.setStatusCode(StatusCode.OK);
                content = objectMapper.writeValueAsString(deckRepository.findByUsername(username));
            }
            else{
                response.setStatusCode(StatusCode.NO_CONTENT);
                content = objectMapper.writeValueAsString("Deck not configured");
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setContent(content);

        return response;
    }

    private Response update(Request request){

        ObjectMapper objectMapper = new ObjectMapper();

        String authorization = request.getAuthorization();

        Response response = new Response();
        response.setContentType(ContentType.APPLICATION_JSON);

        String content = new String();
        Session session;
        String[] cardIds;
        String json = request.getContent();

        //checking if session exists:
        session = sessionRepository.findByToken(authorization);
        try{
            if(session != null){
                String username = sessionRepository.findByToken(request.getAuthorization()).getUsername();

                ObjectMapper mapper = JsonMapper.builder().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS).build();
                cardIds = mapper.readValue(json, String[].class);
                if(cardIds.length == 4){ //if there are enough cards provided

                    int counterValidCards = 0;
                    for (String cardId : cardIds){
                        if(cardRepository.checkCardToUsername(username, cardId) == true)
                            counterValidCards++;
                    }

                    if(counterValidCards == 4){
                        Deck deck = new Deck(username, cardIds[0], cardIds[1],cardIds[2], cardIds[3]);
                        deck = deckRepository.saveDeck(deck);
                        response.setStatusCode(StatusCode.CREATED);
                        content = objectMapper.writeValueAsString(deck);
                    }
                    else{
                        response.setStatusCode(StatusCode.FORBIDDEN);
                        content = objectMapper.writeValueAsString("Cards are not valid/ not owned by the user!");
                    }

                }
                else { //not enough or too many cards
                    response.setStatusCode(StatusCode.FORBIDDEN);
                    content = objectMapper.writeValueAsString("The provided deck did not include the required amount of cards!");
                }

            }
            else{
                response.setStatusCode(StatusCode.UNAUTHORIZED);
                content = objectMapper.writeValueAsString("Access token is missing or invalid!");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        response.setContent(content);
        return response;
    }
}


