package ldg.progettoispw.engineering.dao;

import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.model.Tutor;
import java.util.List;

public interface TutorSearchDAO {
    List<Tutor> findTutorsBySubject(String subject) throws DBException;
}