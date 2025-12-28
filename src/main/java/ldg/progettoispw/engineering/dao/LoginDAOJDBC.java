package ldg.progettoispw.engineering.dao;

import ldg.progettoispw.engineering.exception.DBException;

import java.sql.Connection;
import java.sql.SQLException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginDAOJDBC {
    private final ConnectionFactory connectionFactory = ConnectionFactory.getInstance();

    public static final int SUCCESS = 0;
    public static final int WRONG_PASSWORD = 1;
    public static final int USER_NOT_FOUND = 2;

    public int start(String email, String password) throws DBException {
        List<UserRecord> users = getAllUsersForLogin();
        for (UserRecord user : users) {
            if (user.email.equals(email)) {
                return user.password.equals(password) ? SUCCESS : WRONG_PASSWORD;
            }
        }
        return USER_NOT_FOUND;
    }

    private List<UserRecord> getAllUsersForLogin() throws DBException {
        List<UserRecord> users = new ArrayList<>();
        try (Connection conn = connectionFactory.getDBConnection();
             CallableStatement cs = conn.prepareCall("{call getAllUsersForLogin()}");
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                users.add(new UserRecord(
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getInt("ruolo")
                ));
            }
        } catch (SQLException e) {
            throw new DBException("Errore durante il recupero utenti dal DB", e);
        }
        return users;
    }

    public int getUserRole(String email, String password) throws DBException {
        List<UserRecord> users = getAllUsersForLogin();
        for (UserRecord user : users) {
            if (user.email.equals(email) && user.password.equals(password)) {
                return user.ruolo;
            }
        }
        return -1;
    }

    private static class UserRecord {
        String email;
        String password;
        int ruolo;

        UserRecord(String email, String password, int ruolo) {
            this.email = email;
            this.password = password;
            this.ruolo = ruolo;
        }
    }
}