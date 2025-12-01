package ldg.progettoispw.engineering.dao;

import ldg.progettoispw.engineering.bean.RecensioneBean;
import ldg.progettoispw.engineering.exception.DBException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RecensioneDAO {

    private static final String INSERT_RECENSIONE =
            "INSERT INTO recensioni (tutor_email, student_email, recensione, sentiment_value) " +
                    "VALUES (?, ?, ?, ?)";

    private final ConnectionFactory connectionFactory = ConnectionFactory.getInstance();

    public void insertRecensione(RecensioneBean bean) throws DBException {
        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_RECENSIONE)) {

            ps.setString(1, bean.getTutorEmail());
            ps.setString(2, bean.getStudentEmail());
            ps.setString(3, bean.getRecensione());
            ps.setInt(4, bean.getSentimentValue());

            ps.executeUpdate();

        } catch (SQLException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

            if (msg.contains("foreign key")) {
                throw new DBException("Email non valida (FK violata).", e);
            }
            if (msg.contains("duplicate")) {
                throw new DBException("Recensione già registrata.", e);
            }

            throw new DBException("Errore durante l'inserimento della recensione: " + e.getMessage(), e);
        }
    }
}
