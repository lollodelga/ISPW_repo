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

    // Statistiche (Sentiment Distribution)
    public static final String SELECT_SENTIMENT_DISTRIBUTION =
            """
            SELECT sentiment_value, COUNT(*) AS cnt
            FROM recensioni
            WHERE tutor_email = ?
            GROUP BY sentiment_value
            ORDER BY sentiment_value
            """;

    // Recupero lista recensioni
    public static final String SELECT_BY_TUTOR =
            """
            SELECT id, student_email, recensione, sentiment_value
            FROM recensioni
            WHERE tutor_email = ?
            ORDER BY id DESC
            """;
}