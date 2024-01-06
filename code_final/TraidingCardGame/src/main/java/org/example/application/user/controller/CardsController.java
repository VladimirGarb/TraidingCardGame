package org.example.application.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.example.application.user.model.Card;
import org.example.application.user.model.Package;
import org.example.application.user.model.Session;
import org.example.application.user.repository.CardRepository;
import org.example.application.user.repository.PackageRepository;
import org.example.application.user.repository.SessionRepository;
import org.example.server.dto.Request;
import org.example.server.dto.Response;
import org.example.server.http.ContentType;
import org.example.server.http.Method;
import org.example.server.http.StatusCode;

import java.util.Objects;

public class CardsController {

    private final SessionRepository sessionRepository;
    private final CardRepository cardRepository;

    public CardsController(SessionRepository sessionRepository, CardRepository cardRepository) {
        this.sessionRepository = sessionRepository;
        this.cardRepository = cardRepository;
    }

    public Response handle(Request request) {

        if (request.getMethod().equals(Method.GET.method)) {
            if(request.getAuthorization() != null && sessionRepository.findByToken(request.getAuthorization()) != null)
                return readAllByUsername(sessionRepository.findByToken(request.getAuthorization()).getUsername());
            else{

                Response response = new Response();
                response.setStatusCode(StatusCode.UNAUTHORIZED);
                response.setContentType(ContentType.TEXT_PLAIN);
                response.setContent(StatusCode.UNAUTHORIZED.message);

                return response;
            }
        }

        Response response = new Response();
        response.setStatusCode(StatusCode.METHODE_NOT_ALLOWED);
        response.setContentType(ContentType.TEXT_PLAIN);
        response.setContent(StatusCode.METHODE_NOT_ALLOWED.message);

        return response;
    }

    private Response readAllByUsername(String username) {
        ObjectMapper objectMapper = new ObjectMapper();

        Response response = new Response();
        response.setStatusCode(StatusCode.OK);
        response.setContentType(ContentType.APPLICATION_JSON);
        String content;

        try {
            content = objectMapper.writeValueAsString(cardRepository.allCardsUsername(username));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setContent(content);

        return response;
    }

}
