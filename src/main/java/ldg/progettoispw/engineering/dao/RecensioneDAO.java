package ldg.progettoispw.engineering.dao;

import ldg.progettoispw.engineering.bean.RecensioneBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.model.Recensione;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public List<Integer> getSentimentDistributionByTutor(String tutorEmail) throws DBException {
        String query = """
        SELECT sentiment_value, COUNT(*) AS cnt
        FROM recensione
        WHERE tutor_email = ?
        GROUP BY sentiment_value
        ORDER BY sentiment_value
    """;

        List<Integer> distribution = Arrays.asList(0, 0, 0, 0, 0); // 1-5 stelle

        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, tutorEmail);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int sentiment = rs.getInt("sentiment_value");
                int count = rs.getInt("cnt");

                // sentiment_value va da 1 a 5
                if (sentiment >= 1 && sentiment <= 5) {
                    distribution.set(sentiment - 1, count);
                }
            }

            return distribution;

        } catch (SQLException e) {
            throw new DBException("Errore nel recupero distribuzione sentiment: " + e.getMessage());
        }
    }

    public List<Recensione> getRecensioniByTutor(String tutorEmail) throws DBException {

        String query = """
        SELECT id, student_email, recensione, sentiment_value
        FROM recensioni
        WHERE tutor_email = ?
        ORDER BY id DESC
    """;

        List<Recensione> recensioni = new ArrayList<>();

        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, tutorEmail);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Recensione r = new Recensione();

                r.setId(rs.getInt("id"));
                r.setEmailStudente(rs.getString("student_email"));
                r.setTesto(rs.getString("recensione"));
                r.setSentimentScore(rs.getInt("sentiment_value")); // INT

                recensioni.add(r);
            }

            return recensioni;

        } catch (SQLException e) {
            throw new DBException("Errore nel recupero recensioni: " + e.getMessage(), e);
        }
    }

}
