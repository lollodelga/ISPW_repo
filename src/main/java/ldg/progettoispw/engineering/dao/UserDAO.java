package ldg.progettoispw.engineering.dao;

import ldg.progettoispw.engineering.exception.DBException;

public interface UserDAO {
    /**
     * Ritorna: [nome, cognome, data_nascita, email, password, ruolo]
     */
    String[] takeData(String email, String password) throws DBException;

    /**
     * Ritorna stringa con materie separate da virgola
     */
    String takeSubjects(String email) throws DBException;
}