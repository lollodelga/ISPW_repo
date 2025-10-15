package ldg.progettoispw.model.dao;

import ldg.progettoispw.exception.DBException;
import ldg.progettoispw.model.query.LoginQuery;

import java.sql.Connection;
import java.sql.SQLException;

import java.sql.*;

public class LoginDAO {
    private final ConnectionFactory connectionFactory = ConnectionFactory.getInstance();

    public static final int SUCCESS = 0;
    public static final int WRONG_PASSWORD = 1;
    public static final int USER_NOT_FOUND = 2;

    public int start(String email, String password) throws DBException {
        if (!userExists(email)) {
            return USER_NOT_FOUND;
        }

        if (!checkPassword(email, password)) {
            return WRONG_PASSWORD;
        }

        return SUCCESS;
    }

    private boolean userExists(String email) throws DBException {
        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(LoginQuery.CHECK_USER_EXISTS)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }

        } catch (SQLException e) {
            throw new DBException("Errore durante la verifica dell'esistenza dell'utente", e);
        }
    }

    private boolean checkPassword(String email, String password) throws DBException {
        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(LoginQuery.CHECK_PASSWORD)) {

            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }

        } catch (SQLException e) {
            throw new DBException("Errore durante la verifica della password", e);
        }
    }

    public int getUserRole(String email, String password) throws DBException {
        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(LoginQuery.GET_USER_ROLE)) {

            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ruolo");
                }
                return -1; // ruolo non trovato
            }

        } catch (SQLException e) {
            throw new DBException("Errore durante il recupero del ruolo dell'utente", e);
        }
    }
}
