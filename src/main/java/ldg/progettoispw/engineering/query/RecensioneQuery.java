package ldg.progettoispw.engineering.query;

/**
 * Contiene tutte le stringhe SQL (Query) utilizzate dalla classe RecensioneDAOJDBC.
 */
public class RecensioneQuery {

    private RecensioneQuery() {
        throw new UnsupportedOperationException("Utility class: non può essere istanziata.");
    }

    // Inserimento
    public static final String INSERT_RECENSIONE =
            "INSERT INTO recensioni (tutor_email, student_email, recensione, sentiment_value) VALUES (?, ?, ?, ?)";

    // Recupero lista per tutor (Colonne esplicite, NO SELECT *)
    public static final String SELECT_BY_TUTOR =
            """
            SELECT id, student_email, recensione, sentiment_value
            FROM recensioni
            WHERE tutor_email = ?
            ORDER BY id DESC
            """;

    // Recupero TUTTE le recensioni (per migrazione/export) - Colonne esplicite
    public static final String SELECT_ALL =
            "SELECT tutor_email, student_email, recensione, sentiment_value FROM recensioni";

    // Conteggio totale
    public static final String COUNT_ALL = "SELECT count(*) FROM recensioni";

    // Cancellazione totale
    public static final String DELETE_ALL = "DELETE FROM recensioni";
}