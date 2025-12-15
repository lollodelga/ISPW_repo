package ldg.progettoispw.engineering.query;

/**
 * Contiene le query SQL utilizzate dalla classe UserDAO.
 */
public class UserQuery {

    private UserQuery() {
        throw new UnsupportedOperationException("Utility class: non può essere istanziata.");
    }

    // Recupero dati utente (Login)
    public static final String SELECT_USER_DATA =
            "SELECT nome, cognome, compleanno, ruolo FROM user WHERE email = ? AND password = ?";

    // Recupero materie del tutor
    public static final String SELECT_SUBJECTS_BY_TUTOR = """
            SELECT s.materia 
            FROM assusersubject a 
            JOIN subject s ON a.subject = s.materia 
            WHERE a.tutor = ?
            """;
}