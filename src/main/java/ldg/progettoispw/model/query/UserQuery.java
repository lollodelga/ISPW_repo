package ldg.progettoispw.model.query;

public class UserQuery {
    private UserQuery() {
        throw new IllegalStateException("Utility class");
    }

    // Recupera i dati principali di un utente (nome, cognome, compleanno)
    public static final String TAKE_DATA =
            "SELECT nome, cognome, compleanno FROM user WHERE email = ? AND password = ?";

    // Recupera le materie associate all'utente
    public static final String TAKE_SUBJECTS =
            "SELECT GROUP_CONCAT(s.materia SEPARATOR ', ') AS materie " +
                    "FROM assusersubject aus " +
                    "JOIN subject s ON aus.subject = s.id " +
                    "WHERE aus.tutor = ?";
}
