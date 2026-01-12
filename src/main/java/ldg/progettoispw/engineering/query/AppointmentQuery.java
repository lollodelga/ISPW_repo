package ldg.progettoispw.engineering.query;

/**
 * Contiene tutte le stringhe SQL (Query) utilizzate dalla classe AppointmentDAO.
 */
public class AppointmentQuery {

    private AppointmentQuery() {
        throw new UnsupportedOperationException("Utility class: non può essere istanziata.");
    }

    // Campi
    public static final String FIELD_STUDENTE_EMAIL = "studente_email";
    public static final String FIELD_TUTOR_EMAIL = "tutor_email";

    // Query per il controllo disponibilità
    public static final String CHECK_AVAIL =
            """
            SELECT COUNT(*) 
            FROM appuntamento 
            WHERE tutor_email = ? 
              AND data = ? 
              AND ora = ? 
              AND stato <> 'annullato'
            """;

    // Query per l'inserimento
    public static final String INSERT_APPT =
            "INSERT INTO appuntamento (studente_email, tutor_email, data, ora, stato) VALUES (?, ?, ?, ?, 'in_attesa')";

    // Query per l'aggiornamento dello stato
    public static final String UPDATE_STATUS =
            "UPDATE appuntamento SET stato = ? WHERE studente_email = ? AND tutor_email = ? AND data = ? AND ora = ?";

    // Query di ricerca (in attesa)
    public static final String SEARCH_APP_IN_ATTESA_TUTOR =
            "SELECT * FROM appuntamento WHERE tutor_email = ? AND stato = 'in_attesa'";
    public static final String SEARCH_APP_IN_ATTESA_STUDENTE =
            "SELECT * FROM appuntamento WHERE studente_email = ? AND stato = 'in_attesa'";


    // Query di ricerca (storico: confermati, completati, annullati E PAGATI)
    public static final String SEARCH_APP_BY_STUDENT =
            "SELECT * FROM appuntamento WHERE studente_email = ? AND stato IN ('confermato', 'completato', 'annullato', 'pagato')";

    public static final String SEARCH_APP_BY_TUTOR =
            "SELECT * FROM appuntamento WHERE tutor_email = ? AND stato IN ('confermato', 'completato', 'annullato', 'pagato')";
}