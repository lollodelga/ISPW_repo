package ldg.progettoispw.model.dao;

import ldg.progettoispw.exception.DBException;
import ldg.progettoispw.model.adapter.DateTarget;
import ldg.progettoispw.model.adapter.SQLDateAdapter;
import ldg.progettoispw.model.query.RegistrationQuery;

import java.sql.*;

public class RegistrationDAO {

    private final ConnectionFactory connectionFactory = ConnectionFactory.getInstance();
    // Adapter: converte la data in java.sql.Date
    private final DateTarget dateAdapter = new SQLDateAdapter();

    /**
     * Controlla se l'utente esiste già e, se no, lo inserisce nel DB.
     * @param values [nome, cognome, data, email, password, ..., ruolo]
     * @return 1 se l'utente esiste già, 0 se è stato inserito con successo
     * @throws DBException se avviene un errore SQL o di conversione
     */
    public int checkInDB(String[] values) throws DBException {
        Connection conn = connectionFactory.getDBConnection();

        try {
            // Controlla se esiste già l'email
            try (PreparedStatement checkStmt = conn.prepareStatement(RegistrationQuery.CHECK_EMAIL)) {
                checkStmt.setString(1, values[3]); // email
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {
                    return 1; // esiste già
                }
            }

            // Se non esiste, inserisce l'utente
            try (PreparedStatement insertStmt = conn.prepareStatement(RegistrationQuery.INSERT_USER)) {
                insertStmt.setString(1, values[3]); // email
                insertStmt.setString(2, values[4]); // password
                insertStmt.setString(3, values[0]); // nome
                insertStmt.setString(4, values[1]); // cognome
                insertStmt.setDate(5, dateAdapter.convert(values[2])); // compleanno (adapter!)
                insertStmt.setString(6, values[6]); // ruolo
                insertStmt.executeUpdate();
            }

            return 0; // utente inserito correttamente

        } catch (SQLException e) {
            throw new DBException("Errore durante il controllo/inserimento utente nel DB", e);
        }
    }

    /**
     * Inserisce una nuova materia se non esiste già.
     */
    public void insertSubject(String subject) throws DBException {
        Connection conn = connectionFactory.getDBConnection();

        try {
            // Controlla se la materia esiste
            try (PreparedStatement checkStmt = conn.prepareStatement(RegistrationQuery.CHECK_SUBJECT)) {
                checkStmt.setString(1, subject);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next() && rs.getInt(1) == 0) {
                    // Non esiste → la inserisce
                    try (PreparedStatement insertStmt = conn.prepareStatement(RegistrationQuery.INSERT_SUBJECT)) {
                        insertStmt.setString(1, subject);
                        insertStmt.executeUpdate();
                    }
                }
            }

        } catch (SQLException e) {
            throw new DBException("Errore durante l'inserimento della materia nel DB", e);
        }
    }

    /**
     * Crea l'associazione user <-> subject.
     */
    public void createAssociation(String email, String subject) throws DBException {
        Connection conn = connectionFactory.getDBConnection();

        try (PreparedStatement stmt = conn.prepareStatement(RegistrationQuery.CREATE_ASSOCIATION)) {
            stmt.setString(1, email);
            stmt.setString(2, subject);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DBException("Errore durante la creazione dell'associazione tra utente e materia", e);
        }
    }
}