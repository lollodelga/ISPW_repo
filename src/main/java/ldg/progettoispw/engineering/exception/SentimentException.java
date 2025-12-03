package ldg.progettoispw.engineering.exception;

public class SentimentException extends Exception {
    public SentimentException(String message) {
        super(message);
    }

    public SentimentException(String message, Throwable cause) {
        super(message, cause);
    }
}
