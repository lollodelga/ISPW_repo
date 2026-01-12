package ldg.progettoispw.engineering.dao;

import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.engineering.query.AppointmentQuery;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAOJDBC implements AppointmentDAO {
    private final ConnectionFactory connectionFactory = ConnectionFactory.getInstance();

    @Override
    public void insertAppointment(String studentEmail, String tutorEmail, Date date, Time time) throws DBException {
        if (!isTutorAvailable(tutorEmail, date, time))
            throw new DBException("Orario non disponibile per il tutor selezionato.");

        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(AppointmentQuery.INSERT_APPT)) {

            ps.setString(1, studentEmail);
            ps.setString(2, tutorEmail);
            ps.setDate(3, date);
            ps.setTime(4, time);

            ps.executeUpdate();

        } catch (SQLException e) {
            String msg = e.getMessage();
            if (msg != null && msg.toLowerCase().contains("duplicate")) {
                throw new DBException("Il tutor ha già un appuntamento in questa data/ora.", e);
            } else {
                throw new DBException("Errore DB durante inserimento appuntamento: " + msg, e);
            }
        }
    }

    @Override
    public boolean isTutorAvailable(String tutorEmail, Date date, Time time) throws DBException {
        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(AppointmentQuery.CHECK_AVAIL)) {

            ps.setString(1, tutorEmail);
            ps.setDate(2, date);
            ps.setTime(3, time);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count == 0;
                }
                return false;
            }

        } catch (SQLException e) {
            throw new DBException("Errore durante il controllo disponibilità: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateAppointmentStatus(String studentEmail, String tutorEmail, Date date, Time time, String newStatus) throws DBException {
        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(AppointmentQuery.UPDATE_STATUS)) {

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

    @Override
    public List<AppointmentBean> getAppuntamentiInAttesa(String email, boolean isTutor) throws DBException {
        String query = isTutor ? AppointmentQuery.SEARCH_APP_IN_ATTESA_TUTOR : AppointmentQuery.SEARCH_APP_IN_ATTESA_STUDENTE;

        try {
            return executeQueryAndMap(query, email);
        } catch (SQLException e) {
            throw new DBException("Errore durante il recupero degli appuntamenti in attesa per l'utente: " + email, e);
        }
    }

    @Override
    public List<AppointmentBean> getAppuntamentiByEmail(String email, int tipo) throws DBException {
        String query;
        switch (tipo) {
            case 0 -> query = AppointmentQuery.SEARCH_APP_BY_STUDENT;
            case 1 -> query = AppointmentQuery.SEARCH_APP_BY_TUTOR;
            default -> throw new DBException("Tipo email non valido. 0 = studente, 1 = tutor");
        }

        try {
            return executeQueryAndMap(query, email);

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
    }

    /**
     * Metodo privato di supporto per eseguire una query SELECT che prende un'email come parametro
     * e restituisce una lista di AppointmentBean.
     */
    private List<AppointmentBean> executeQueryAndMap(String query, String emailParam) throws SQLException {
        List<AppointmentBean> appuntamenti = new ArrayList<>();

        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, emailParam);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AppointmentBean bean = new AppointmentBean();
                    bean.setId(rs.getInt("id"));
                    bean.setStudenteEmail(rs.getString(AppointmentQuery.FIELD_STUDENTE_EMAIL));
                    bean.setTutorEmail(rs.getString(AppointmentQuery.FIELD_TUTOR_EMAIL));
                    bean.setData(rs.getDate("data"));
                    bean.setOra(rs.getTime("ora"));
                    bean.setStato(rs.getString("stato"));
                    appuntamenti.add(bean);
                }
            }
        }
        return appuntamenti;
    }
}