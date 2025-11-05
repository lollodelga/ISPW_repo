package ldg.progettoispw.engineering.dao;

import ldg.progettoispw.engineering.exception.DBException;

import java.sql.*;

public class AppointmentDAO {
    private final ConnectionFactory connectionFactory = ConnectionFactory.getInstance();

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

}