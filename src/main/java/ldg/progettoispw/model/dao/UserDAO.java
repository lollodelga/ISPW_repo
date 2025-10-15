package ldg.progettoispw.model.dao;

import ldg.progettoispw.exception.DBException;
import ldg.progettoispw.model.query.UserQuery;

import java.sql.*;
import java.text.SimpleDateFormat;

public class UserDAO {
    private final ConnectionFactory cf = ConnectionFactory.getInstance();

    public String[] takeData(String email, String password) throws DBException {
        String[] data = new String[6];

        try (Connection conn = cf.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(UserQuery.TAKE_DATA)) {

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
                }
            }

        } catch (SQLException e) {
            throw new DBException("Errore durante il recupero dei dati dell'utente", e);
        }

        return data;
    }

    public String takeSubjects(String email) throws DBException {
        try (Connection conn = cf.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(UserQuery.TAKE_SUBJECTS)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("materie");
                }
                return null;
            }

        } catch (SQLException e) {
            throw new DBException("Errore durante il recupero delle materie dell'utente", e);
        }
    }
}