package ldg.progettoispw.controller;

import ldg.progettoispw.exception.*;
import ldg.progettoispw.model.applicativo.LoginSessionManager;
import ldg.progettoispw.model.bean.UserBean;
import ldg.progettoispw.model.dao.LoginDAO;
import ldg.progettoispw.model.dao.UserDAO;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginCtrlApplicativo {

    private static final Logger logger = Logger.getLogger(LoginCtrlApplicativo.class.getName());

    private final LoginDAO loginDAO = new LoginDAO();
    private final UserDAO userDAO = new UserDAO();

    /**
     * Verifica le credenziali dell'utente e, se corrette,
     * salva la sessione e restituisce il ruolo (1 = Tutor, 2 = Studente).
     */
    public int verificaCredenziali(String email, String password)
            throws InvalidEmailException, IncorrectPasswordException, UserDoesNotExistException, DBException {

        // ✅ Validazione sintattica
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new InvalidEmailException("Campi vuoti o email non valida.");
        }

        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,7}$");
        Matcher matcher = emailPattern.matcher(email);
        if (!matcher.matches()) {
            throw new InvalidEmailException("Formato email non valido.");
        }

        // ✅ Verifica con il DB
        int result = loginDAO.start(email, password);

        switch (result) {
            case LoginDAO.SUCCESS -> {
                String[] data = userDAO.takeData(email, password);
                data[5] = userDAO.takeSubjects(email);

                UserBean userBean = new UserBean();
                userBean.setOfAll(data);
                LoginSessionManager.saveUserSession(userBean);

                return loginDAO.getUserRole(email, password); // 1 = Tutor, 2 = Studente
            }
            case LoginDAO.WRONG_PASSWORD -> throw new IncorrectPasswordException("Password errata.");
            case LoginDAO.USER_NOT_FOUND -> throw new UserDoesNotExistException("Utente non trovato.");
            default -> throw new DBException("Errore sconosciuto nel database.");
        }
    }
}