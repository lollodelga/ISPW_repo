package ldg.progettoispw.engineering.dao;

import ldg.progettoispw.engineering.bean.RecensioneBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.engineering.query.RecensioneQuery; // Import della classe Query
import ldg.progettoispw.model.Recensione;
import ldg.progettoispw.util.RecensioneDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecensioneDAOJDBC implements RecensioneDAO {

    private static final Logger logger = Logger.getLogger(RecensioneDAOJDBC.class.getName());
    private final ConnectionFactory connectionFactory = ConnectionFactory.getInstance();

    @Override
    public void insertRecensione(RecensioneBean bean) throws DBException {
        // Uso la costante dalla classe Query
        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(RecensioneQuery.INSERT_RECENSIONE)) {

            ps.setString(1, bean.getTutorEmail());
            ps.setString(2, bean.getStudentEmail());
            ps.setString(3, bean.getRecensione());
            ps.setInt(4, bean.getSentimentValue());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DBException("Errore inserimento JDBC: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Recensione> getRecensioniByTutor(String tutorEmail) throws DBException {
        List<Recensione> list = new ArrayList<>();

        // Uso SELECT_BY_TUTOR (colonne esplicite: id, student_email, recensione, sentiment_value)
        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(RecensioneQuery.SELECT_BY_TUTOR)) {

            ps.setString(1, tutorEmail);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Recensione r = new Recensione();
                    // Mappatura sicura basata sulle colonne esplicite della query
                    r.setId(rs.getInt("id"));
                    r.setEmailStudente(rs.getString("student_email"));
                    r.setTesto(rs.getString("recensione"));
                    r.setSentimentScore((int) rs.getDouble("sentiment_value"));
                    list.add(r);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DBException("Errore lettura JDBC: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isEmpty() {
        // Uso COUNT_ALL
        try (Connection conn = connectionFactory.getDBConnection();
             java.sql.Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(RecensioneQuery.COUNT_ALL)) {

            if (rs.next()) return rs.getInt(1) == 0;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante il controllo isEmpty su JDBC", e);
        }
        return true;
    }

    @Override
    public List<RecensioneBean> getAllRecensioni() throws DBException {
        List<RecensioneBean> list = new ArrayList<>();

        // Uso SELECT_ALL invece di SELECT *
        try (Connection conn = connectionFactory.getDBConnection();
             java.sql.Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(RecensioneQuery.SELECT_ALL)) {

            while (rs.next()) {
                RecensioneBean b = new RecensioneBean();
                b.setTutorEmail(rs.getString("tutor_email"));
                b.setStudentEmail(rs.getString("student_email"));
                b.setRecensione(rs.getString("recensione"));
                b.setSentimentValue((int) rs.getDouble("sentiment_value"));
                list.add(b);
            }
        } catch (SQLException e) {
            throw new DBException("Errore migration JDBC", e);
        }
        return list;
    }

    @Override
    public void deleteAll() throws DBException {
        // Uso DELETE_ALL
        try (Connection conn = connectionFactory.getDBConnection();
             java.sql.Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(RecensioneQuery.DELETE_ALL);

        } catch (SQLException e) {
            throw new DBException("Errore svuotamento JDBC", e);
        }
    }
}