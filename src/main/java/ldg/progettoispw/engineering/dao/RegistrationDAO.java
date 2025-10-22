package ldg.progettoispw.engineering.dao;

import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.model.User;

import java.sql.*;

public class RegistrationDAO {

    private final ConnectionFactory connectionFactory = ConnectionFactory.getInstance();

    public int checkInDB(User user) throws DBException {
        String checkQuery = "SELECT COUNT(*) FROM user WHERE email = ?";
        String insertQuery = "INSERT INTO user (nome, cognome, compleanno, email, password, ruolo) VALUES (?, ?, ?, ?, ?, ?)";

        try (
                Connection conn = connectionFactory.getDBConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery)
        ) {
            // 1️⃣ Controlla se l’utente esiste già
            checkStmt.setString(1, user.getEmail());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return 1; // utente già esistente
                }
            }

            // 2️⃣ Inserisce il nuovo utente
            insertStmt.setString(1, user.getName());
            insertStmt.setString(2, user.getSurname());
            insertStmt.setDate(3, user.getBirthDate());
            insertStmt.setString(4, user.getEmail());
            insertStmt.setString(5, user.getPassword());
            insertStmt.setString(6, user.getRole());
            insertStmt.executeUpdate();

            return 0; // successo

        } catch (SQLException e) {
            throw new DBException("Errore durante il controllo/inserimento utente nel DB", e);
        }
    }

    public void insertSubject(String subject) throws DBException {
        String checkQuery = "SELECT COUNT(*) FROM subject WHERE materia = ?";
        String insertQuery = "INSERT INTO subject (materia) VALUES (?)";

        try (
                Connection conn = connectionFactory.getDBConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery)
        ) {
            checkStmt.setString(1, subject);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    insertStmt.setString(1, subject);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DBException("Errore durante l'inserimento della materia nel DB", e);
        }
    }


    public void createAssociation(String email, String subject) throws DBException {
        String insertQuery = "INSERT INTO assusersubject (tutor, subject) VALUES (?, ?)";

        try (
                Connection conn = connectionFactory.getDBConnection();
                PreparedStatement ps = conn.prepareStatement(insertQuery)
        ) {
            ps.setString(1, email);
            ps.setString(2, subject);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Errore durante la creazione dell'associazione tra utente e materia", e);
        }
    }
}