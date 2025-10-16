package ldg.progettoispw.model.dao;

import ldg.progettoispw.exception.DBException;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final ConnectionFactory cf = ConnectionFactory.getInstance();

    /**
     * Recupera tutti i dati principali dell'utente in base a email e password.
     * Ritorna: nome, cognome, data di nascita, email, password, ruolo
     */
    public String[] takeData(String email, String password) throws DBException {
        String[] data = new String[6];
        String query = "SELECT nome, cognome, compleanno, ruolo FROM user WHERE email = ? AND password = ?";

        try (Connection conn = cf.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data[0] = rs.getString("nome");
                    data[1] = rs.getString("cognome");

                    Date date = rs.getDate("compleanno");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    data[2] = (date != null) ? sdf.format(date) : null;

                    data[3] = email;
                    data[4] = password;
                    data[5] = rs.getString("ruolo");
                }
            }

        } catch (SQLException e) {
            throw new DBException("Errore durante il recupero dei dati dell'utente", e);
        }

        return data;
    }

    /**
     * Recupera tutte le materie associate a un utente tutor.
     * Ritorna una stringa con le materie separate da virgola, es. "Matematica, Inglese, Storia"
     */
    public String takeSubjects(String email) throws DBException {
        String query = """
            SELECT s.materia 
            FROM assusersubject a 
            JOIN subject s ON a.subject = s.materia 
            WHERE a.tutor = ?
        """;

        List<String> subjects = new ArrayList<>();

        try (Connection conn = cf.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    subjects.add(rs.getString("materia"));
                }
            }

        } catch (SQLException e) {
            throw new DBException("Errore durante il recupero delle materie dell'utente", e);
        }

        // Unisci tutte le materie separate da virgole
        return String.join(", ", subjects);
    }
}