package ldg.progettoispw.exception;

public class WrongPasswordException extends Exception {

    public WrongPasswordException() {
        super("Password errata!");
    }

    public WrongPasswordException(String message) {
        super(message);
    }
}