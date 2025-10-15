package ldg.progettoispw.model.query;

public class RegistrationQuery {
    private RegistrationQuery() {
        throw new IllegalStateException("Utility class");
    }
    // Controlla se esiste già un utente con quella email
    public static final String CHECK_EMAIL =
            "SELECT COUNT(*) FROM user WHERE email = ?";

    // Inserisce un nuovo utente
    public static final String INSERT_USER =
            "INSERT INTO user (email, password, nome, cognome, compleanno, ruolo) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    // Controlla se una materia esiste
    public static final String CHECK_SUBJECT =
            "SELECT COUNT(*) FROM subject WHERE materia = ?";

    // Inserisce una materia
    public static final String INSERT_SUBJECT =
            "INSERT INTO subject (materia) VALUES (?)";

    // Crea associazione tra utente e materia
    public static final String CREATE_ASSOCIATION =
            "INSERT INTO assusersubject (tutor, subject) VALUES (?, ?)";
}
