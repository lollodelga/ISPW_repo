package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.applicativo.DateUtils;
import ldg.progettoispw.engineering.applicativo.Validator;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.engineering.dao.RegistrationDAO; // IMPORT INTERFACCIA
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.engineering.factory.DAOFactory;   // IMPORT FACTORY
import ldg.progettoispw.model.User;

import java.sql.Date;
import java.util.logging.Logger;

public class RegistrationCtrlApplicativo {

    private static final Logger logger = Logger.getLogger(RegistrationCtrlApplicativo.class.getName());

    // Costanti di ritorno (codici di errore/successo)
    public static final int OK = 0;
    public static final int USER_EXISTS = 1;
    public static final int EMPTY_FIELD = 2;
    public static final int INVALID_EMAIL = 3;
    public static final int INVALID_PASSWORD = 4;
    public static final int INVALID_DATE = 5;
    public static final int DB_ERROR = 6;

    // 1. Dichiara l'INTERFACCIA, non l'implementazione concreta
    private final RegistrationDAO registrationDAO;

    public RegistrationCtrlApplicativo() {
        // 2. Chiedi alla Factory quale versione usare (JDBC o MEMORY)
        this.registrationDAO = DAOFactory.getRegistrationDAO();
    }

    public int registerUser(UserBean bean) {
        // Validazione input
        int validationResult = validateInput(bean);
        if (validationResult != OK) return validationResult;

        try {
            // Conversione Bean → Model
            // Nota: Assicurati che DateUtils gestisca bene la stringa vuota o nulla se non validata prima
            Date sqlDate = DateUtils.toSqlDate(bean.getBirthDate());

            User user = new User(
                    bean.getName(),
                    bean.getSurname(),
                    sqlDate,
                    bean.getEmail(),
                    bean.getPassword(),
                    bean.getRole()
            );

            // 3. Usa il DAO ottenuto dalla Factory
            // Nota: Il tuo metodo checkInDB fa ANCHE l'inserimento se l'utente non esiste.
            int exists = registrationDAO.checkInDB(user);

            if (exists == 1) {
                return USER_EXISTS; // Utente già presente
            }

            // Inserimento materie (solo se è un tutor o se il bean ha materie)
            if (bean.getSubjects() != null && !bean.getSubjects().isEmpty()) {
                String[] subjects = bean.getSubjects().split(",");
                for (String subject : subjects) {
                    String trimmed = subject.trim();
                    if (!trimmed.isEmpty()) {
                        registrationDAO.insertSubject(trimmed);
                        registrationDAO.createAssociation(user.getEmail(), trimmed);
                    }
                }
            }

        } catch (DBException e) {
            logger.warning("Errore persistenza durante la registrazione: " + e.getMessage());
            return DB_ERROR;
        } catch (Exception e) {
            logger.severe("Errore generico in registrazione: " + e.getMessage());
            return DB_ERROR;
        }

        return OK;
    }

    private int validateInput(UserBean bean) {
        if (bean.getName().isEmpty() || bean.getSurname().isEmpty() ||
                bean.getBirthDate().isEmpty() || bean.getEmail().isEmpty() ||
                bean.getPassword().isEmpty()) {
            // Nota: Ho tolto il check su subjects qui, perché uno studente potrebbe non averne
            // Se subjects è obbligatorio per tutti, rimettilo pure.
            return EMPTY_FIELD;
        }

        if (!Validator.isValidEmail(bean.getEmail())) return INVALID_EMAIL;
        if (!Validator.isValidPassword(bean.getPassword())) return INVALID_PASSWORD;
        if (!Validator.isValidDate(bean.getBirthDate())) return INVALID_DATE;

        return OK;
    }
}