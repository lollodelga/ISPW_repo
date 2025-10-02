package ldg.progettoispw.model.dao;

import ldg.progettoispw.exception.DBException;

import java.sql.*;
import java.text.SimpleDateFormat;

public class UserDAO {
    private final ConnectionFactory cf = ConnectionFactory.getInstance();

    public String[] takeData(String email, String password) throws DBException {
        String[] data = new String[6];
        Connection conn = cf.getDBConnection();
        try (
             CallableStatement cs = conn.prepareCall("{call takeData(?, ?, ?, ?, ?)}")) {

            cs.setString(1, email);
            cs.setString(2, password);
            cs.registerOutParameter(3, Types.VARCHAR);
            cs.registerOutParameter(4, Types.VARCHAR);
            cs.registerOutParameter(5, Types.DATE);
            cs.execute();

            data[0] = cs.getString(3); // nome
            data[1] = cs.getString(4); // cognome

            Date date = cs.getDate(5);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            data[2] = (date != null) ? sdf.format(date) : null;

            data[3] = email;
            data[4] = password;

        } catch (SQLException e) {
            throw new DBException("Errore durante il recupero dei dati dell'utente", e);
        }

        return data;
    }

    public String takeSubjects(String email) throws DBException {
        Connection conn = cf.getDBConnection();
        try (
             CallableStatement cs = conn.prepareCall("{call getSubjects(?, ?)}")) {

            cs.setString(1, email);
            cs.registerOutParameter(2, Types.VARCHAR);
            cs.execute();
            return cs.getString(2);

        } catch (SQLException e) {
            throw new DBException("Errore durante il recupero delle materie dell'utente", e);
        }
    }
}