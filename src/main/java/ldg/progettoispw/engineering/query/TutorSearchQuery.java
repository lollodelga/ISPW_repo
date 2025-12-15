package ldg.progettoispw.engineering.query;

/**
 * Contiene le query SQL utilizzate dalla classe TutorSearchDAO.
 */
public class TutorSearchQuery {

    private TutorSearchQuery() {
        throw new UnsupportedOperationException("Utility class: non può essere istanziata.");
    }

    // Query per cercare tutor in base alla materia
    public static final String FIND_TUTORS_BY_SUBJECT = """
        SELECT u.email, u.nome, u.cognome, s.materia
        FROM user u
        JOIN assusersubject aus ON u.email = aus.tutor
        JOIN subject s ON aus.subject = s.materia
        WHERE u.ruolo = 1 AND s.materia LIKE ?
        ORDER BY u.cognome, u.nome
        """;
}