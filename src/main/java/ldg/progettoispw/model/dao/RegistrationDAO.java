package ldg.progettoispw.model.dao;

import ldg.progettoispw.exception.DBException;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class RegistrationDAO {

    private final ConnectionFactory connectionFactory = ConnectionFactory.getInstance();

    // Verifica se l'utente esiste già e, se no, lo inserisce
    public int checkInDB(String[] values) throws DBException {
        Connection conn = connectionFactory.getDBConnection();

        String checkQuery = "SELECT COUNT(*) FROM user WHERE email = ?";
        String insertQuery = "INSERT INTO user (nome, cognome, compleanno, email, password, ruolo) VALUES (?, ?, ?, ?, ?, ?)";

        try (
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery)
        ) {
            // Controlla se l'utente esiste già
            checkStmt.setString(1, values[3]); // email
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return 1; // Utente già esistente
                }
            }

            // Inserisce il nuovo utente
            insertStmt.setString(1, values[0]); // nome
            insertStmt.setString(2, values[1]); // cognome
            insertStmt.setDate(3, convertToSQLDate(values[2])); // compleanno
            insertStmt.setString(4, values[3]); // email
            insertStmt.setString(5, values[4]); // password
            insertStmt.setString(6, values[6]); // ruolo
            insertStmt.executeUpdate();

            return 0; // Inserimento riuscito

        } catch (SQLException e) {
            throw new DBException("Errore durante il controllo/inserimento utente nel DB", e);
        }
    }

    // Inserisce una materia (se non già presente)
    public void insertSubject(String subject) throws DBException {
        String checkQuery = "SELECT COUNT(*) FROM subject WHERE materia = ?";
        String insertQuery = "INSERT INTO subject (materia) VALUES (?)";

        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

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

    // Crea l'associazione user <-> subject
    public void createAssociation(String email, String subject) throws DBException {
        String insertQuery = "INSERT INTO assusersubject (tutor, subject) VALUES (?, ?)";

        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(insertQuery)) {

            ps.setString(1, email);
            ps.setString(2, subject);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DBException("Errore durante la creazione dell'associazione tra utente e materia", e);
        }
    }

    // Conversione data in formato SQL
    private java.sql.Date convertToSQLDate(String dateString) throws DBException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            java.util.Date parsedDate = sdf.parse(dateString);
            return new java.sql.Date(parsedDate.getTime());
        } catch (ParseException e) {
            throw new DBException("Formato data invalido. Usa yyyy-MM-dd: " + dateString, e);
        }
    }
}
