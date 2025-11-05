package ldg.progettoispw.engineering.dao;

import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.model.Tutor;

import java.sql.*;
import java.util.*;

public class TutorSearchDAO {

    private final ConnectionFactory connectionFactory = ConnectionFactory.getInstance();

    private static final String FIND_TUTORS_BY_SUBJECT = """
        SELECT u.email, u.nome, u.cognome, s.materia
        FROM user u
        JOIN assusersubject aus ON u.email = aus.tutor_email
        JOIN subject s ON aus.subject_id = s.id
        WHERE u.ruolo = 1 AND s.materia LIKE ?
        ORDER BY u.cognome, u.nome
        """;

    public List<Tutor> findTutorsBySubject(String subject) throws DBException {
        Map<String, Tutor> tutorMap = new LinkedHashMap<>();

        String query = """
        SELECT u.email, u.nome, u.cognome, s.materia
        FROM user u
        JOIN assusersubject aus ON u.email = aus.tutor
        JOIN subject s ON aus.subject = s.materia
        WHERE u.ruolo = 1 AND s.materia LIKE ?
        ORDER BY u.cognome, u.nome
        """;

        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, "%" + subject + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String email = rs.getString("email");
                    String nome = rs.getString("nome");
                    String cognome = rs.getString("cognome");
                    String materia = rs.getString("materia");

                    Tutor tutor = tutorMap.get(email);
                    if (tutor == null) {
                        tutor = new Tutor(email, nome, cognome);
                        tutorMap.put(email, tutor);
                    }

                    tutor.addMateria(materia);
                }
            }

        } catch (SQLException e) {
            throw new DBException("Errore durante la ricerca dei tutor per materia.", e);
        }

        return new ArrayList<>(tutorMap.values());
    }


}
