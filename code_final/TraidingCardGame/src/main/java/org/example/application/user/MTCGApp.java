package org.example.application.user;

import org.example.application.user.controller.*;
import org.example.application.user.repository.*;
import org.example.server.Application;
import org.example.server.dto.Request;
import org.example.server.dto.Response;
import org.example.server.http.ContentType;
import org.example.server.http.StatusCode;

public class MTCGApp implements Application {

    private UserController userController;
    private SessionController sessionController;
    private PackageController packageController;
    private PackageAcquireController packageAcquireController;
    private CardsController cardsController;
    private DeckController deckController;
    private StatsController statsController;
    private ScoreboardController scoreboardController;

    public MTCGApp() {
        UserRepository userRepository = new UserDataBaseRepository();
        SessionRepository sessionRepository = new SessionDataBaseRepository();
        PackageRepository packageRepository = new PackageDataBaseRepository();
        CardRepository cardRepository = new CardDataBaseRepository();
        DeckRepository deckRepository = new DeckDataBaseRepository();

        this.userController = new UserController(userRepository, sessionRepository);
        this.sessionController = new SessionController(userRepository, sessionRepository);
        this.packageController = new PackageController(sessionRepository, packageRepository, cardRepository);
        this.packageAcquireController = new PackageAcquireController(sessionRepository, packageRepository, cardRepository, userRepository);
        this.cardsController = new CardsController(sessionRepository, cardRepository);
        this.deckController = new DeckController(sessionRepository, deckRepository, cardRepository);
        this.statsController = new StatsController(userRepository, sessionRepository);
        this.scoreboardController = new ScoreboardController(userRepository, sessionRepository);

    }

    @Override
    public Response handle(Request request) {
        if (request.getPath().startsWith("/users")) {
            return userController.handle(request);
        }

        if (request.getPath().startsWith("/sessions")) {
            return sessionController.handle(request);
        }

        if (request.getPath().startsWith("/packages")) {
            return packageController.handle(request);
        }

        if (request.getPath().startsWith("/transactions/packages")) {
            return packageAcquireController.handle(request);
        }

        if (request.getPath().startsWith("/cards")) {
            return cardsController.handle(request);
        }

        if (request.getPath().startsWith("/deck")) {
            return deckController.handle(request);
        }

        if (request.getPath().startsWith("/stats")) {
            return statsController.handle(request);
        }

        if (request.getPath().startsWith("/score")) {
            return scoreboardController.handle(request);
        }

        Response response = new Response();
        response.setStatusCode(StatusCode.NOT_FOUND);
        response.setContentType(ContentType.TEXT_PLAIN);
        response.setContent(StatusCode.NOT_FOUND.message);

        return response;
    }
}
