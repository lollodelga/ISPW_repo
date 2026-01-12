package ldg.progettoispw.engineering.dao;

import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.model.User;

public class LoginDAOMemory implements LoginDAO {

    @Override
    public int start(String email, String password) throws DBException {
        // Scorro la lista condivisa in UserDAOMemory
        for (User u : UserDAOMemory.USERS_LIST) {
            if (u.getEmail().equals(email)) {
                if (u.getPassword().equals(password)) {
                    return SUCCESS;
                } else {
                    return WRONG_PASSWORD;
                }
            }
        }
        return USER_NOT_FOUND;
    }

    @Override
    public int getUserRole(String email, String password) throws DBException {
        for (User u : UserDAOMemory.USERS_LIST) {
            if (u.getEmail().equals(email) && u.getPassword().equals(password)) {
                String r = u.getRole();
                // Adatta i return in base alla tua logica applicativa (int vs String)
                if ("TUTOR".equalsIgnoreCase(r)) return 1;
                if ("STUDENTE".equalsIgnoreCase(r)) return 2; // O 0, controlla i tuoi codici
                return -1;
            }
        }
        return -1;
    }
}