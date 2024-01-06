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

public class ScoreboardController {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public ScoreboardController(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public Response handle(Request request) {

        if (request.getMethod().equals(Method.GET.method)) {

            if(request.getAuthorization() != null && sessionRepository.findByToken(request.getAuthorization()) != null) {
                return readScoreboard();
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

    private Response readScoreboard() {

        ObjectMapper objectMapper = new ObjectMapper();

        Response response = new Response();
        response.setStatusCode(StatusCode.OK);
        response.setContentType(ContentType.APPLICATION_JSON);
        String content;
        try {
            List<StatsVO> scoreboard = userRepository.getScoreboard();
            content = objectMapper.writeValueAsString(scoreboard);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        response.setContent(content);
        return response;
    }
}
