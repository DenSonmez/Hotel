package dk.lyngby.exception;

public class ExpiredTokenException extends Exception{

    public ExpiredTokenException(){
        super("Authorization token has expired.");
    }

    public ExpiredTokenException(String message) {
        super(message);
    }

    public int getStatusCode() {
        // status code 401 bad or expired token
        return 401;
    }

}