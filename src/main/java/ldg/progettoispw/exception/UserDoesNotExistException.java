package ldg.progettoispw.exception;

public class UserDoesNotExistException extends Exception {

    public UserDoesNotExistException() {
        super("L'utente non esiste!");
    }

    public UserDoesNotExistException(String message) {
        super(message);
    }
}
