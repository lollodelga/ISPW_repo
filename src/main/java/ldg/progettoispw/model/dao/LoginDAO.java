package ldg.progettoispw.model.dao;

import ldg.progettoispw.exception.DBException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class LoginDAO {
    private final ConnectionFactory connectionFactory = ConnectionFactory.getInstance();

    // Codici di risposta
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
        Connection conn = connectionFactory.getDBConnection();
        try (
             CallableStatement cs = conn.prepareCall("{call checkExistence(?, ?)}")) {

            cs.setString(1, email);
            cs.registerOutParameter(2, Types.BOOLEAN);
            cs.execute();
            return cs.getBoolean(2);

        } catch (SQLException e) {
            throw new DBException("Errore durante la verifica dell'esistenza dell'utente", e);
        }
    }

    private boolean checkPassword(String email, String password) throws DBException {
        Connection conn = connectionFactory.getDBConnection();
        try (
             CallableStatement cs = conn.prepareCall("{call checkPassword(?, ?, ?)}")) {

            cs.setString(1, email);
            cs.setString(2, password);
            cs.registerOutParameter(3, Types.BOOLEAN);
            cs.execute();
            return cs.getBoolean(3);

        } catch (SQLException e) {
            throw new DBException("Errore durante la verifica della password", e);
        }
    }

    public int getUserRole(String email, String password) throws DBException {
        Connection conn = connectionFactory.getDBConnection();
        try (
             CallableStatement cs = conn.prepareCall("{call getUserRole(?, ?, ?)}")) {

            cs.setString(1, email);
            cs.setString(2, password);
            cs.registerOutParameter(3, Types.INTEGER);

            cs.execute();

            return cs.getInt(3);

        } catch (SQLException e) {
            throw new DBException("Errore durante il recupero del ruolo dell'utente", e);
        }
    }
}