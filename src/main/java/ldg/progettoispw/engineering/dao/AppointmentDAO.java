package ldg.progettoispw.engineering.dao;

import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.exception.DBException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    private final ConnectionFactory connectionFactory = ConnectionFactory.getInstance();

    private static final String FIELD_STUDENTE_EMAIL = "studente_email";
    private static final String FIELD_TUTOR_EMAIL = "tutor_email";

    //query utilizzate nei metodi
    private static final String CHECK_AVAIL =
            """
            SELECT COUNT(*) 
            FROM appuntamento 
            WHERE tutor_email = ? 
              AND data = ? 
              AND ora = ? 
              AND stato <> 'annullato'
            """;
    private static final String INSERT_APPT =
            "INSERT INTO appuntamento (studente_email, tutor_email, data, ora, stato) VALUES (?, ?, ?, ?, 'in_attesa')";
    private static final String UPDATE_STATUS =
            "UPDATE appuntamento SET stato = ? WHERE studente_email = ? AND tutor_email = ? AND data = ? AND ora = ?";
    private static final String SEARCH_APP_IN_ATTESA_TUTOR =
            "SELECT * FROM appuntamento WHERE tutor_email = ? AND stato = 'in_attesa'";
    private static final String SEARCH_APP_IN_ATTESA_STUDENTE =
            "SELECT * FROM appuntamento WHERE studente_email = ? AND stato = 'in_attesa'";
    private static final String SEARCH_APP_BY_STUDENT =
            "SELECT * FROM appuntamento WHERE studente_email = ? AND stato IN ('confermato', 'completato', 'annullato')";
    private static final String SEARCH_APP_BY_TUTOR =
            "SELECT * FROM appuntamento WHERE tutor_email = ? AND stato IN ('confermato', 'completato', 'annullato')";


    public void insertAppointment(String studentEmail, String tutorEmail, Date date, Time time) throws DBException {
        // controlla disponibilità e FK/ruolo dovrebbero essere verificate in applicativo prima
        if (!isTutorAvailable(tutorEmail, date, time))
            throw new DBException("Orario non disponibile per il tutor selezionato.");

        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_APPT)) {

            ps.setString(1, studentEmail);
            ps.setString(2, tutorEmail);
            ps.setDate(3, date);
            ps.setTime(4, time);

            ps.executeUpdate();

        } catch (SQLException e) {
            // Se è violazione vincolo unique o FK, restituisci messaggio leggibile
            String msg = e.getMessage();
            if (msg != null && msg.toLowerCase().contains("duplicate")) {
                throw new DBException("Il tutor ha già un appuntamento in questa data/ora.", e);
            } else {
                throw new DBException("Errore DB durante inserimento appuntamento: " + msg, e);
            }
        }
    }

    public boolean isTutorAvailable(String tutorEmail, Date date, Time time) throws DBException {
        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(CHECK_AVAIL)) {

            ps.setString(1, tutorEmail);
            ps.setDate(2, date);
            ps.setTime(3, time);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    // true se nessun appuntamento attivo o in attesa in quella data/ora
                    return count == 0;
                }
                return false;
            }

        } catch (SQLException e) {
            throw new DBException("Errore durante il controllo disponibilità: " + e.getMessage(), e);
        }
    }

    public void updateAppointmentStatus(String studentEmail, String tutorEmail, Date date, Time time, String newStatus) throws DBException {
        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_STATUS)) {

            ps.setString(1, newStatus);
            ps.setString(2, studentEmail);
            ps.setString(3, tutorEmail);
            ps.setDate(4, date);
            ps.setTime(5, time);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DBException("Errore durante aggiornamento stato: " + e.getMessage(), e);
        }
    }

    public List<AppointmentBean> getAppuntamentiInAttesa(String email, boolean isTutor) throws DBException {
        List<AppointmentBean> appuntamenti = new ArrayList<>();

        String query = isTutor ? SEARCH_APP_IN_ATTESA_TUTOR : SEARCH_APP_IN_ATTESA_STUDENTE;

        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AppointmentBean bean = new AppointmentBean();
                    bean.setId(rs.getInt("id"));
                    bean.setStudenteEmail(rs.getString(FIELD_STUDENTE_EMAIL));
                    bean.setTutorEmail(rs.getString(FIELD_TUTOR_EMAIL));
                    bean.setData(rs.getDate("data"));
                    bean.setOra(rs.getTime("ora"));
                    bean.setStato(rs.getString("stato"));
                    appuntamenti.add(bean);
                }
            }

        } catch (SQLException e) {
            throw new DBException("Errore durante il recupero degli appuntamenti in attesa per l'utente: " + email, e);
        }

        return appuntamenti;
    }

    public List<AppointmentBean> getAppuntamentiByEmail(String email, int tipo) throws DBException {
        List<AppointmentBean> appuntamenti = new ArrayList<>();

        // Seleziona query in base al tipo
        String query;
        switch (tipo) {
            case 0 -> query = SEARCH_APP_BY_STUDENT;
            case 1 -> query = SEARCH_APP_BY_TUTOR;
            default -> throw new DBException("Tipo email non valido. 0 = studente, 1 = tutor");
        }

        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AppointmentBean bean = new AppointmentBean();
                    bean.setId(rs.getInt("id"));
                    bean.setStudenteEmail(rs.getString(FIELD_STUDENTE_EMAIL));
                    bean.setTutorEmail(rs.getString(FIELD_TUTOR_EMAIL));
                    bean.setData(rs.getDate("data"));
                    bean.setOra(rs.getTime("ora"));
                    bean.setStato(rs.getString("stato"));
                    appuntamenti.add(bean);
                }
            }

        } catch (SQLException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

            if (msg.contains("duplicate")) {
                throw new DBException("Appuntamento già presente nel database.", e);
            } else if (msg.contains("foreign key")) {
                throw new DBException("Email non valida o vincolo di chiave esterna violato.", e);
            } else if (msg.contains("connection") || msg.contains("timeout")) {
                throw new DBException("Errore di connessione al database.", e);
            } else {
                throw new DBException("Errore generico durante il recupero degli appuntamenti: " + e.getMessage(), e);
            }
        }

        return appuntamenti;
    }

}