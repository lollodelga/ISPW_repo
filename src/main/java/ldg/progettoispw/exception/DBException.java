package ldg.progettoispw.exception;

import java.io.Serial;

public class DBException extends Exception {

    public DBException() {
        super("Errore di connessione o operazione sul database!");
    }


    public DBException (String message)
    {
        super(message);
    }

    public DBException(String message, Throwable cause) {
        super(message, cause);
    }
}
