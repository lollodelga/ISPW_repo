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
              AND date = ? 
              AND time = ? 
              AND status <> 'annullato'
            """;

    // Query per l'inserimento
    public static final String INSERT_APPT =
            "INSERT INTO appuntamento (studente_email, tutor_email, date, time, status) VALUES (?, ?, ?, ?, 'in_attesa')";

    // Query per l'aggiornamento dello status
    public static final String UPDATE_STATUS =
            "UPDATE appuntamento SET status = ? WHERE studente_email = ? AND tutor_email = ? AND date = ? AND time = ?";

    // Query di ricerca (in attesa)
    public static final String SEARCH_APP_IN_ATTESA_TUTOR =
            "SELECT * FROM appuntamento WHERE tutor_email = ? AND status = 'in_attesa'";
    public static final String SEARCH_APP_IN_ATTESA_STUDENTE =
            "SELECT * FROM appuntamento WHERE studente_email = ? AND status = 'in_attesa'";


    // Query di ricerca (storico: confermati, completati, annullati E PAGATI)
    public static final String SEARCH_APP_BY_STUDENT =
            "SELECT * FROM appuntamento WHERE studente_email = ? AND status IN ('confermato', 'completato', 'annullato', 'pagato')";

    public static final String SEARCH_APP_BY_TUTOR =
            "SELECT * FROM appuntamento WHERE tutor_email = ? AND status IN ('confermato', 'completato', 'annullato', 'pagato')";
}