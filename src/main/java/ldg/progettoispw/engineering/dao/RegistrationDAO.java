package ldg.progettoispw.engineering.dao;

import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.model.User;

public interface RegistrationDAO {
    int checkInDB(User user) throws DBException;
    void insertSubject(String subject) throws DBException;
    void createAssociation(String email, String subject) throws DBException;
}