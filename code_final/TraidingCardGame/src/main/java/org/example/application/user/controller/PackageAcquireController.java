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
import org.example.application.user.repository.UserRepository;
import org.example.server.dto.Request;
import org.example.server.dto.Response;
import org.example.server.http.ContentType;
import org.example.server.http.Method;
import org.example.server.http.StatusCode;

import java.util.Objects;

public class PackageAcquireController {
    private final SessionRepository sessionRepository;
    private final PackageRepository packageRepository;
    private final CardRepository cardRepository;

    private final UserRepository userRepository;

    public PackageAcquireController(SessionRepository sessionRepository, PackageRepository packageRepository, CardRepository cardRepository, UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.packageRepository = packageRepository;
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    public Response handle(Request request) {

        if (request.getMethod().equals(Method.POST.method)) {
            //String username = sessionRepository.findByToken(request.getAuthorization()).getUsername();
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

        Response response = new Response();
        response.setStatusCode(StatusCode.METHODE_NOT_ALLOWED);
        response.setContentType(ContentType.TEXT_PLAIN);
        response.setContent(StatusCode.METHODE_NOT_ALLOWED.message);

        return response;
    }

    private Response create(Request request) {

        ObjectMapper objectMapper = new ObjectMapper();

        String authorization = request.getAuthorization();

        Response response = new Response();
        response.setStatusCode(StatusCode.OK);
        response.setContentType(ContentType.APPLICATION_JSON);

        Session session;
        //checking if session exists:
        session = sessionRepository.findByToken(authorization);

        String content;

        try {
            content = objectMapper.writeValueAsString(Objects.requireNonNullElse(session, "Error: not logged in!"));
            //if the session exists:
            if (session != null){

                Package cardPackage = packageRepository.getFirstAvailablePackage();
                //checking if any packages are available
                if(cardPackage != null) {

                    //checking if there are at least 5 coins to pay for the package
                    if(userRepository.payForPackage(session.getUsername())){
                        // giving the cards to the user by saving the username in the card entries:
                        cardRepository.appendUsername(cardPackage.getCard1(), session.getUsername());
                        cardRepository.appendUsername(cardPackage.getCard2(), session.getUsername());
                        cardRepository.appendUsername(cardPackage.getCard3(), session.getUsername());
                        cardRepository.appendUsername(cardPackage.getCard4(), session.getUsername());
                        cardRepository.appendUsername(cardPackage.getCard5(), session.getUsername());

                        //deleting the card package from the database
                        packageRepository.deleteById(cardPackage.getId());

                        content = objectMapper.writeValueAsString(cardPackage);
                    }
                    else {
                        response.setStatusCode(StatusCode.FORBIDDEN);
                        content = "Error: not enough money";
                    }
                }
                else{
                    response.setStatusCode(StatusCode.NOT_FOUND);
                    content = "Error: no package available";
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setContent(content);

        return response;
    }
}
