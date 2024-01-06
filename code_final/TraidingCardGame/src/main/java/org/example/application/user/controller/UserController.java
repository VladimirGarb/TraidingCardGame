package org.example.application.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.application.user.model.User;
import org.example.application.user.repository.SessionRepository;
import org.example.application.user.repository.UserRepository;
import org.example.server.dto.Request;
import org.example.server.dto.Response;
import org.example.server.http.ContentType;
import org.example.server.http.Method;
import org.example.server.http.StatusCode;

import java.util.Objects;

public class UserController {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public UserController(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public Response handle(Request request) {

        if (request.getMethod().equals(Method.POST.method)) {
            //String username = sessionRepository.findByToken(request.getAuthorization()).getUsername();
            return create(request);
        }

        if (request.getMethod().equals(Method.PUT.method)) {
            String username = sessionRepository.findByToken(request.getAuthorization()).getUsername();
            if(request.getPath().endsWith("/"+username)) {
                return changeData(request);
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
            String username = sessionRepository.findByToken(request.getAuthorization()).getUsername();
            if(request.getPath().endsWith("/"+username)) {
                return readUser(username);
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

    private Response changeData(Request request) {
        ObjectMapper objectMapper = new ObjectMapper();

        Response response = new Response();
        response.setStatusCode(StatusCode.OK);
        response.setContentType(ContentType.APPLICATION_JSON);
        String content;
        String json = request.getContent();
        User user;
        String username = sessionRepository.findByToken(request.getAuthorization()).getUsername();

        try {
            user = objectMapper.readValue(json, User.class);
            content = objectMapper.writeValueAsString(userRepository.updateUser(username, user.getName(), user.getBio(), user.getImage()));
            if(content == null){
                response.setStatusCode(StatusCode.NOT_FOUND);
                response.setContent("User not found");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setContent(content);

        return response;
    }

    private Response readUser(String username) {
        ObjectMapper objectMapper = new ObjectMapper();

        Response response = new Response();
        response.setStatusCode(StatusCode.OK);
        response.setContentType(ContentType.APPLICATION_JSON);
        String content;
        try {
            content = objectMapper.writeValueAsString(userRepository.findByUsername(username));
            if(content == null){
                response.setStatusCode(StatusCode.NOT_FOUND);
                response.setContent("User not found");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setContent(content);

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

        user = userRepository.save(user);

        Response response = new Response();
        response.setStatusCode(StatusCode.CREATED);
        response.setContentType(ContentType.APPLICATION_JSON);
        String content;
        try {
            content = objectMapper.writeValueAsString(Objects.requireNonNullElse(user, "User with same username already registered"));
            if(user == null){
                response.setStatusCode(StatusCode.CONFLICT);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setContent(content);

        return response;
    }
}
