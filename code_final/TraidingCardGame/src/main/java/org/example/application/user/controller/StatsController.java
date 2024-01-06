package org.example.application.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.application.user.model.StatsVO;
import org.example.application.user.model.User;
import org.example.application.user.repository.SessionRepository;
import org.example.application.user.repository.UserRepository;
import org.example.server.dto.Request;
import org.example.server.dto.Response;
import org.example.server.http.ContentType;
import org.example.server.http.Method;
import org.example.server.http.StatusCode;

import java.util.List;

public class StatsController {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public StatsController(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public Response handle(Request request) {

        if (request.getMethod().equals(Method.GET.method)) {

            String username = sessionRepository.findByToken(request.getAuthorization()).getUsername();
            if(request.getAuthorization() != null && sessionRepository.findByToken(request.getAuthorization()) != null) {
                return readStats(username);
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

    private Response readStats(String username) {

        ObjectMapper objectMapper = new ObjectMapper();

        Response response = new Response();
        response.setStatusCode(StatusCode.OK);
        response.setContentType(ContentType.APPLICATION_JSON);
        String content;
        try {
            User user = userRepository.findByUsername(username);
            content = objectMapper.writeValueAsString(new StatsVO(user.getName(), user.getElo(),user.getWins(), user.getLosses()));
            if(user == null){
                response.setStatusCode(StatusCode.NOT_FOUND);
                response.setContent("User not found");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setContent(content);

        return response;
    }
}
