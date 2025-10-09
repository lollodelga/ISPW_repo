package ldg.progettoispw.exception;

public class InvalidEmailException extends Exception {

    public InvalidEmailException() {
        super("Email non valida!");
    }

    public InvalidEmailException(String message) {
        super(message);
    }
}