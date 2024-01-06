package org.example.application.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.example.application.user.model.Card;
import org.example.application.user.model.Package;
import org.example.application.user.model.Session;
import org.example.application.user.model.User;
import org.example.application.user.repository.CardRepository;
import org.example.application.user.repository.PackageRepository;
import org.example.application.user.repository.SessionRepository;
import org.example.server.dto.Request;
import org.example.server.dto.Response;
import org.example.server.http.ContentType;
import org.example.server.http.Method;
import org.example.server.http.StatusCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PackageController {

    private final SessionRepository sessionRepository;
    private final PackageRepository packageRepository;
    private final CardRepository cardRepository;

    public PackageController(SessionRepository sessionRepository, PackageRepository packageRepository, CardRepository cardRepository) {
        this.sessionRepository = sessionRepository;
        this.packageRepository = packageRepository;
        this.cardRepository = cardRepository;
    }

    public Response handle(Request request) {

        if (request.getMethod().equals(Method.POST.method)) {
            if(request.getAuthorization() != null && sessionRepository.findByToken(request.getAuthorization()) != null) {
                return create(request);
            }
            else{
                Response response = new Response();
                response.setStatusCode(StatusCode.UNAUTHORIZED);
                response.setContentType(ContentType.TEXT_PLAIN);
                response.setContent("Access token is missing or invalid");
                return response;
            }
        }

        if (request.getMethod().equals(Method.GET.method)) {
            if(request.getAuthorization() != null && sessionRepository.findByToken(request.getAuthorization()).getUsername().equals("admin")) {
                return readAll();
            }
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

    private Response readAll() {
        ObjectMapper objectMapper = new ObjectMapper();

        Response response = new Response();
        response.setStatusCode(StatusCode.OK);
        response.setContentType(ContentType.APPLICATION_JSON);
        String content;
        try {
            content = objectMapper.writeValueAsString(packageRepository.findAll());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setContent(content);

        return response;
    }

    private Response create(Request request) {

        ObjectMapper objectMapper = new ObjectMapper();

        String json = request.getContent();
        String authorization = request.getAuthorization();

        Card[] cards;
        try {
            ObjectMapper mapper = JsonMapper.builder().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS).build();
            cards = mapper.readValue(json, Card[].class);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Response response = new Response();
        response.setStatusCode(StatusCode.CREATED);
        response.setContentType(ContentType.APPLICATION_JSON);

        Session session;
        //checking if session exists:
        session = sessionRepository.findByToken(authorization);

        String content;

        try {
            content = objectMapper.writeValueAsString(Objects.requireNonNullElse(session, "Error: not logged in!"));
            //if the session exists:
            if (session != null){

                //save all the cards in the database and their ids in an array:
                for (Card card: cards)
                {
                    cardRepository.save(card);
                }

                //save the package
                Package cardPackage = new Package(cards[0].getId(), cards[1].getId(), cards[2].getId(), cards[3].getId(), cards[4].getId());
                packageRepository.save(cardPackage);

                content = objectMapper.writeValueAsString(cardPackage);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setContent(content);

        return response;
    }
}
