package org.example.application.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.application.user.model.Session;
import org.example.application.user.model.User;
import org.example.application.user.repository.SessionRepository;
import org.example.application.user.repository.UserRepository;
import org.example.server.dto.Request;
import org.example.server.dto.Response;
import org.example.server.http.ContentType;
import org.example.server.http.Method;
import org.example.server.http.StatusCode;
import org.example.application.user.model.Session;

public class SessionController {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    public SessionController(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public Response handle(Request request) {

        if (request.getMethod().equals(Method.POST.method)) {
            return create(request);
        }

        Response response = new Response();
        response.setStatusCode(StatusCode.METHODE_NOT_ALLOWED);
        response.setContentType(ContentType.TEXT_PLAIN);
        response.setContent(StatusCode.METHODE_NOT_ALLOWED.message);

        return response;
    }

    private Response create(Request request) {
        ObjectMapper objectMapper = new ObjectMapper();

        String json = request.getContent();
        User user;
        try {
            user = objectMapper.readValue(json, User.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        user = userRepository.login(user);

        Response response = new Response();
        response.setStatusCode(StatusCode.OK);
        response.setContentType(ContentType.APPLICATION_JSON);
        String content;
        try {
            if(user == null) {
                response.setStatusCode(StatusCode.UNAUTHORIZED);
                content = objectMapper.writeValueAsString("Error: wrong credentials!");
            }
            else {
                Session session = new Session();
                session.setUsername(user.getUsername());
                session.setToken(user.getUsername());
                content = objectMapper.writeValueAsString(session);
                // adding the new session to the sessions table
                sessionRepository.save(session);

            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setContent(content);

        return response;
    }
}
