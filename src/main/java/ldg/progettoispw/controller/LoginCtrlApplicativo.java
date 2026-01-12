package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.api.PythonServerLauncher;
import ldg.progettoispw.engineering.applicativo.LoginSessionManager;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.engineering.dao.LoginDAO;
import ldg.progettoispw.engineering.dao.UserDAO;
import ldg.progettoispw.engineering.exception.*;
import ldg.progettoispw.engineering.factory.DAOFactory;
import ldg.progettoispw.engineering.factory.PersistenceConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginCtrlApplicativo {

    // 1. Dichiara le INTERFACCE (Mai le classi JDBC direttamente)
    private final LoginDAO loginDAO;
    private final UserDAO userDAO;

    public LoginCtrlApplicativo() {
        // 2. Ottieni le istanze dalla Factory (così supporta JDBC, CSV e DEMO)
        this.loginDAO = DAOFactory.getLoginDAO();
        this.userDAO = DAOFactory.getUserDAO();
    }

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

        int result = loginDAO.start(email, password);

        // Usa le costanti dell'INTERFACCIA LoginDAO, non della classe JDBC
        switch (result) {
            case LoginDAO.SUCCESS -> {

                // Recupero dati completi
                String[] data = userDAO.takeData(email, password);

                /* * NOTA: Nel tuo codice originale sovrascrivevi data[5] (Ruolo) con le materie.
                 * Assicurati che UserBean.setOfAll si aspetti le materie all'indice 5.
                 * Se data[5] era il ruolo, qui lo stai perdendo nel bean,
                 * ma lo recuperi correttamente dopo con getUserRole.
                 */
                String subjects = userDAO.takeSubjects(email);
                // Se subjects è vuoto e data[5] contiene il ruolo, valuta se sovrascrivere o concatenare.
                // Per ora lascio la tua logica originale:
                if (subjects != null && !subjects.isEmpty()) {
                    data[5] = subjects;
                }

                // Creazione e salvataggio sessione
                UserBean userBean = new UserBean();
                userBean.setOfAll(data);
                LoginSessionManager.saveUserSession(userBean);

                // In Demo, non avendo un DB vero, il server Python potrebbe non funzionare.
                if (PersistenceConfig.getInstance().getType() != PersistenceConfig.PersistenceType.DEMO) {
                    try {
                        PythonServerLauncher.launch();
                    } catch (Exception e) {
                        // Logghiamo ma non blocchiamo il login se il server python fallisce
                        System.err.println("Avviso: Impossibile avviare server Python: " + e.getMessage());
                    }
                }

                // Restituisco il ruolo (1 o 2) usando il metodo del DAO
                return loginDAO.getUserRole(email, password);
            }
            case LoginDAO.WRONG_PASSWORD -> throw new IncorrectPasswordException("Password errata.");
            case LoginDAO.USER_NOT_FOUND -> throw new UserDoesNotExistException("Utente non trovato.");
            default -> throw new DBException("Errore sconosciuto nel sistema di persistenza.");
        }
    }
}