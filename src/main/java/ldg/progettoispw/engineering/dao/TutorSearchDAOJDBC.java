package ldg.progettoispw.engineering.dao;

import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.engineering.query.TutorSearchQuery;
import ldg.progettoispw.model.Tutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TutorSearchDAOJDBC implements TutorSearchDAO {

    private final ConnectionFactory connectionFactory = ConnectionFactory.getInstance();

    @Override
    public List<Tutor> findTutorsBySubject(String subject) throws DBException {
        // Uso una LinkedHashMap per mantenere l'ordine di inserimento
        // ed evitare duplicati dello stesso tutor se insegna più materie simili.
        Map<String, Tutor> tutorMap = new LinkedHashMap<>();

        // Utilizzo la query dalla classe esterna
        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(TutorSearchQuery.FIND_TUTORS_BY_SUBJECT)) {

            ps.setString(1, "%" + subject + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String email = rs.getString("email");
                    String nome = rs.getString("nome");
                    String cognome = rs.getString("cognome");
                    String materia = rs.getString("materia");

                    // Logica di aggregazione: se il tutor è già nella mappa, lo recupero,
                    // altrimenti lo creo e lo aggiungo.
                    Tutor tutor = tutorMap.computeIfAbsent(email, k -> new Tutor(k, nome, cognome));

                    // Aggiungo la materia alla lista delle competenze del tutor
                    tutor.addMateria(materia);
                }
            }

        } catch (SQLException e) {
            throw new DBException("Errore durante la ricerca dei tutor per materia: " + e.getMessage(), e);
        }

        return new ArrayList<>(tutorMap.values());
    }
}