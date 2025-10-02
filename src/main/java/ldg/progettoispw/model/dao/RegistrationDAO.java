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
        try (
                CallableStatement cstmt = conn.prepareCall("{call checkEmail(?,?,?,?,?,?,?)}")
        ) {
            cstmt.setString(1, values[3]); // email
            cstmt.setString(2, values[4]); // password
            cstmt.setString(3, values[0]); // nome
            cstmt.setString(4, values[1]); // cognome
            cstmt.setDate(5, convertToSQLDate(values[2])); // data di nascita
            cstmt.setString(6, values[6]); // ruolo
            cstmt.registerOutParameter(7, Types.INTEGER);

            cstmt.execute();
            return cstmt.getInt(7);

        } catch (SQLException e) {
            throw new DBException("Errore durante il controllo dell'email nel DB", e);
        }
    }

    // Inserisce una materia (se non già presente)
    public void insertSubject(String subject) throws DBException {
        Connection conn = connectionFactory.getDBConnection();
        try (
                CallableStatement cstmt = conn.prepareCall("{call insertSubject(?)}")
        ) {
            cstmt.setString(1, subject);
            cstmt.execute();
        } catch (SQLException e) {
            throw new DBException("Errore durante l'inserimento della materia nel DB", e);
        }
    }

    // Crea l'associazione user <-> subject
    public void createAssociation(String email, String subject) throws DBException {
        Connection conn = connectionFactory.getDBConnection();
        try (
                CallableStatement cstmt = conn.prepareCall("{call creaAssociazione(?,?)}")
        ) {
            cstmt.setString(1, email);
            cstmt.setString(2, subject);
            cstmt.execute();
        } catch (SQLException e) {
            throw new DBException("Errore durante la creazione dell'associazione tra utente e materia", e);
        }
    }

    // Conversione data in formato SQL
    private Date convertToSQLDate(String dateString) throws DBException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            java.util.Date parsedDate = sdf.parse(dateString);
            return new Date(parsedDate.getTime());
        } catch (ParseException e) {
            throw new DBException("Formato data invalido. Usa yyyy-MM-dd: " + dateString, e);
        }
    }
}
