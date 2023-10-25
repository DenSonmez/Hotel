package dk.lyngby.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import dk.lyngby.config.ApplicationConfig;
import dk.lyngby.dto.UserDTO;
import dk.lyngby.exception.ApiException;
import dk.lyngby.exception.AuthorizationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenFactory {

    // Singleton
    private static TokenFactory instance;

    // Properties
    private final String ISSUER = Objects.requireNonNull(getProperties())[0];
    private final String TOKEN_EXPIRE_TIME = Objects.requireNonNull(getProperties())[1];
    private final String SECRET_KEY = Objects.requireNonNull(getProperties())[2];

    // Logger
    private final Logger LOGGER = LoggerFactory.getLogger(TokenFactory.class);

    // SignToken class
    private final SignVerifyToken signature = new SignVerifyToken(ISSUER, TOKEN_EXPIRE_TIME, SECRET_KEY);

    public static TokenFactory getInstance() {
        if (instance == null) {
            instance = new TokenFactory();
        }
        return instance;
    }

    // Get properties from pom file
    private String[] getProperties() {
        try {
            // Opret et array til at gemme de hentede egenskaber (properties).
            String[] properties = new String[3];

            // Hent udstederen (issuer), udløbstiden for token og den hemmelige nøgle fra en konfigurationsfil ved hjælp af ApplicationConfig.
            properties[0] = ApplicationConfig.getProperty("issuer");
            properties[1] = ApplicationConfig.getProperty("token.expiration.time");
            properties[2] = ApplicationConfig.getProperty("secret.key");

            // Returner de hentede egenskaber som et array.
            return properties;
        } catch (IOException e) {
            // Håndter eventuelle undtagelser, hvis der opstår fejl under hentning af egenskaber, og log fejlen.
            LOGGER.error("Could not get properties", e);
        }

        // Returner null, hvis der opstår en undtagelse (fejl) under hentning af egenskaber.
        return null;
    }


    public String[] parseJsonObject(String jsonString, Boolean tryLogin) throws ApiException {
        try {
            // Definer en liste af gyldige roller.
            List<String> roles = Arrays.asList("user", "admin", "manager");

            // Opret en ObjectMapper til at analysere JSON-data.
            ObjectMapper mapper = new ObjectMapper();

            // Analysér JSON-strengen og konverter den til et Map-objekt.
            Map json = mapper.readValue(jsonString, Map.class);

            // Hent brugernavn og adgangskode fra JSON-dataen.
            String username = json.get("username").toString();
            String password = json.get("password").toString();

            String role = "";

            // Hvis vi ikke kun prøver at logge ind (tryLogin = false), hent rollen fra JSON-dataen og valider den.
            if (!tryLogin) {
                role = json.get("role").toString();

                // Hvis den valgte rolle ikke er gyldig (findes ikke i listen af gyldige roller), kast en ApiException.
                if (!roles.contains(role)) throw new ApiException(400, "Role not valid");
            }

            // Returner brugernavn, adgangskode og rolle som et array af strenge.
            return new String[]{username, password, role};

        } catch (JsonProcessingException | NullPointerException e) {
            // Håndter eventuelle undtagelser, hvis der opstår problemer med JSON eller manglende data, og kast en ApiException med en fejlmeddelelse og statuskode 400.
            throw new ApiException(400, "Malformed JSON Supplied");
        }
    }


    // enne metode tager brugernavn og en mængde af roller som input, konverterer rollerne til en kommasepareret streng, opretter en udløbstidspunkt og
    // kalder en anden metode (signature.signToken) for at generere og signere JWT-tokenen med brugernavn, roller og udløbstidspunkt
    public String createToken(String userName, Set<String> roles) throws ApiException {
        // Opret en String Builder for at konvertere roller til en kommasepareret streng.
        try {
            StringBuilder res = new StringBuilder();

            // Gennemgå roller og tilføj dem til StringBuilder med komma som separator.
            for (String string : roles) {
                res.append(string);
                res.append(",");
            }

            // Fjern det sidste komma, hvis der er roller i strengen.
            String rolesAsString = !res.isEmpty() ? res.substring(0, res.length() - 1) : "";

            // Opret en ny dato for at bestemme udløbstidspunktet for tokenen.
            Date date = new Date();

            // Kald en anden metode (signature.signToken) for at oprette og signere JWT-tokenen
            // med brugernavn, roller og udløbstidspunkt som parametre.
            return signature.signToken(userName, rolesAsString, date);
        } catch (JOSEException e) {
            // Håndter eventuelle undtagelser ved oprettelse af tokenen og kast en ApiException med fejlmeddelelse og statuskode 500.
            throw new ApiException(500, "Could not create token");
        }
    }


    // Denne metode bruges til at verificere JWT-tokenen ved at bruge den samme hemmelige nøgle, der blev brugt til at signere den.
    public UserDTO verifyToken(String token) throws ApiException, AuthorizationException {
        try {
            SignedJWT signedJWT = signature.parseTokenAndVerify(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            return signature.getJWTClaimsSet(claimsSet);
        } catch (ParseException | JOSEException e) {
            throw new ApiException(401, e.getMessage());
        }
    }
}