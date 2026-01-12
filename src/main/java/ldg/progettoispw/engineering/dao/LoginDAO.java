package ldg.progettoispw.engineering.dao;

import ldg.progettoispw.engineering.exception.DBException;

public interface LoginDAO {
    int start(String email, String password) throws DBException;
    int getUserRole(String email, String password) throws DBException;

    // Costanti utili
    int SUCCESS = 0;
    int WRONG_PASSWORD = 1;
    int USER_NOT_FOUND = 2;
}