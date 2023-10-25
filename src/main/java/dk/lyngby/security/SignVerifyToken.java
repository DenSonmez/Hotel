package dk.lyngby.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import dk.lyngby.dto.UserDTO;
import dk.lyngby.dto.UserDTO;
import dk.lyngby.exception.AuthorizationException;

import java.text.ParseException;
import java.util.Date;


public class SignVerifyToken {

    private final String ISSUER, TOKEN_EXPIRE_TIME, SECRET_KEY;


    public SignVerifyToken(String ISSUER, String TOKEN_EXPIRE_TIME, String SECRET_KEY) {
        this.ISSUER = ISSUER;
        this.TOKEN_EXPIRE_TIME = TOKEN_EXPIRE_TIME;
        this.SECRET_KEY = SECRET_KEY;
    }


    // Denne metode tager brugerens navn, roller og udløbstidspunkt
    // som input og genererer en signatur for JWT'en. Den inkluderer brugerens oplysninger som claims i JWT'en.
    public String signToken(String userName, String rolesAsString, Date date) throws JOSEException {
        JWTClaimsSet claims = createClaims(userName, rolesAsString, date);
        JWSObject jwsObject = createHeaderAndPayload(claims);
        return signTokenWithSecretKey(jwsObject);
    }

    //Denne metode opretter et JWTClaimsSet-objekt, der indeholder påstande
    // (claims) såsom brugernavn, udsteder og udløbstidspunkt baseret på de givne oplysninger.
    private JWTClaimsSet createClaims(String username, String rolesAsString, Date date) {
        return new JWTClaimsSet.Builder()
                .subject(username)
                .issuer(ISSUER)
                .claim("username", username)
                .claim("roles", rolesAsString)
                .expirationTime(new Date(date.getTime() + Integer.parseInt(TOKEN_EXPIRE_TIME)))
                .build();
    }

    //Denne metode opretter JWT-header og payload ved at kombinere JWTClaimsSet-objektet med oplysninger om JWT-algoritmen.
    // Headeren indeholder information om algoritmen, mens payload indeholder de definerede claims.
    private JWSObject createHeaderAndPayload(JWTClaimsSet claimsSet) {
        return new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(claimsSet.toJSONObject()));
    }
//Denne metode bruges til at signere JWT-tokenen ved at tilføje en kryptografisk
// signatur ved hjælp af en delt hemmelig nøgle. Det returnerer den endelige JWT-token som en streng.
    private String signTokenWithSecretKey(JWSObject jwsObject) {
        try {
            JWSSigner signer = new MACSigner(SECRET_KEY.getBytes());
            jwsObject.sign(signer);
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Signing failed", e);
        }
    }


    //Denne metode bruges til at verificere JWT-tokenen ved at bruge den samme hemmelige nøgle, der blev brugt til at signere den.
    public SignedJWT parseTokenAndVerify(String token) throws ParseException, JOSEException, AuthorizationException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());

        if (!signedJWT.verify(verifier)) {
            throw new AuthorizationException(401, "Invalid token signature");
        }
        return signedJWT;
    }



    // Denne metode bruges til at hente brugeroplysninger og roller fra JWT's claims.
    // Den kontrollerer også, om JWT'en er udløbet, og kaster en undtagelse, hvis det er tilfældet.
    // Den returnerer brugeroplysningerne i form af en brugerrepræsentation (UserDto).

    public UserDTO getJWTClaimsSet(JWTClaimsSet claimsSet) throws AuthorizationException {

        if (new Date().after(claimsSet.getExpirationTime()))
            throw new AuthorizationException(401, "Token is expired");

        String username = claimsSet.getClaim("username").toString();
        String roles = claimsSet.getClaim("roles").toString();
        String[] rolesArray = roles.split(",");

        return new UserDTO(username, rolesArray);
    }

}