package dk.lyngby.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.lyngby.config.HibernateConfig;
import dk.lyngby.dao.impl.UserDAO;
import dk.lyngby.exception.ApiException;
import dk.lyngby.exception.AuthorizationException;
import dk.lyngby.model.User;
import dk.lyngby.security.TokenFactory;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.Set;

public class UserController {

    private final UserDAO userDao;
    private final TokenFactory tokenFactory = TokenFactory.getInstance();

    public UserController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        userDao = UserDAO.getInstance(emf);
    }

    public void login(Context ctx) throws ApiException, AuthorizationException {
        // Først hentes brugeroplysninger fra anmodningen ved at kalde getUserInfos(ctx, true).tryLogin-parameteren er sand, da det er et login-forsøg.
        String[] userInfos = getUserInfos(ctx, true);
        //Derefter forsøger metoden at få brugeren ved at kalde getVerifiedOrRegisterUser, hvor det kontrolleres, om brugeren eksisterer og adgangskoden er korrekt.
        User user = getVerfiedOrRegisterUser(userInfos[0], userInfos[1], "", false);
        //Hvis brugeren er gyldig, genereres en JWT-token ved at kalde getToken med brugernavn og roller.
        String token = getToken(userInfos[0], user.getRolesAsStrings());

        // Create response
        ctx.status(200);
        //Til sidst oprettes en JSON-respons med brugernavn og JWT-token ved hjælp af createResponse.
        ctx.result(createResponse(userInfos[0], token));
    }


    public void register(Context ctx) throws ApiException, AuthorizationException {
        //først hentes brugeroplysninger fra anmodningen ved at kalde getUserInfos(ctx, false). tryLogin-parameteren er falsk, da det er en oprettelsesoperation.
        String[] userInfos = getUserInfos(ctx, false);
        //Derefter forsøger metoden at registrere brugeren ved at kalde getVerifiedOrRegisterUser. Hvis brugeren allerede findes, kastes en undtagelse.
        User user = getVerfiedOrRegisterUser(userInfos[0], userInfos[1], userInfos[2], true);
        //Hvis registreringen er vellykket, genereres en JWT-token ved at kalde getToken med brugernavn og roller.
        String token = getToken(userInfos[0], user.getRolesAsStrings());

        // Create response
        ctx.res().setStatus(201);
        //Til sidst oprettes en JSON-respons med brugernavn og JWT-token ved hjælp af createResponse.
        ctx.result(createResponse(userInfos[0], token));
    }

    //Denne metode opretter en JSON-respons med brugernavnet og JWT-tokenen og returnerer det som en JSON-streng.
    private String createResponse(String username, String token) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode responseJson = mapper.createObjectNode();
        responseJson.put("username", username);
        responseJson.put("token", token);
        return responseJson.toString();
    }

  //Denne private metode bruges til at hente brugeroplysninger fra anmodningen og analysere dem.
  // Dette er nødvendigt, da brugeroplysningerne normalt sendes som JSON-data i anmodningen.
    private String[] getUserInfos(Context ctx, boolean tryLogin) throws ApiException {
        String request = ctx.body();
        return tokenFactory.parseJsonObject(request, tryLogin);
    }

    //metode bruges til at få en eksisterende bruger eller registrere en ny bruger afhængigt af handlingen (login eller oprettelse).
    // Det er nødvendigt, da det afgør, om brugeren skal kontrolleres eller oprettes.
    private User getVerfiedOrRegisterUser(String username, String password, String role, boolean isCreate) throws AuthorizationException {
        return isCreate ? userDao.registerUser(username, password, role) : userDao.getVerifiedUser(username, password);
    }

    //denne private metode bruges til at generere en JWT-token baseret på brugernavn og roller.
    // Dette er nødvendigt for at give brugeren en autentikationsmekanisme.
    private String getToken(String username, Set<String> userRoles) throws ApiException {
        return tokenFactory.createToken(username, userRoles);
    }
}