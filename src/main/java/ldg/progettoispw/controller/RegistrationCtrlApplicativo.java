package ldg.progettoispw.controller;

import ldg.progettoispw.exception.DBException;
import ldg.progettoispw.model.applicativo.Validator;
import ldg.progettoispw.model.bean.UserBean;
import ldg.progettoispw.model.dao.RegistrationDAO;

import java.util.logging.Logger;

public class RegistrationCtrlApplicativo {

    private static final Logger logger = Logger.getLogger(RegistrationCtrlApplicativo.class.getName());

    private static final int OK = 0;
    private static final int USER_EXISTS = 1;
    private static final int EMPTY_FIELD = 2;
    private static final int INVALID_EMAIL = 3;
    private static final int INVALID_PASSWORD = 4;
    private static final int INVALID_DATE = 5;
    private static final int DB_ERROR = 6;

    public int registerUser(UserBean bean) {
        int result = validateInput(bean);
        if (result != OK) return result;

        try {
            RegistrationDAO dao = new RegistrationDAO();
            String[] values = bean.getArray();

            int exists = dao.checkInDB(values);
            if (exists == 1) return USER_EXISTS;

            // Inserimento materie
            String[] subjects = bean.getSubjects().split(",");
            for (String subject : subjects) {
                String trimmed = subject.trim();
                dao.insertSubject(trimmed);
                dao.createAssociation(bean.getEmail(), trimmed);
            }

        } catch (DBException e) {
            logger.warning("Errore DB durante la registrazione: " + e.getMessage());
            return DB_ERROR;
        }

        return OK;
    }

    private int validateInput(UserBean bean) {
        if (bean.getName().isEmpty() || bean.getSurname().isEmpty() ||
                bean.getBirthDate().isEmpty() || bean.getEmail().isEmpty() ||
                bean.getPassword().isEmpty() || bean.getSubjects().isEmpty()) {
            return EMPTY_FIELD;
        }

        if (!Validator.isValidEmail(bean.getEmail())) return INVALID_EMAIL;
        if (!Validator.isValidPassword(bean.getPassword())) return INVALID_PASSWORD;
        if (!Validator.isValidDate(bean.getBirthDate())) return INVALID_DATE;

        return OK;
    }
}