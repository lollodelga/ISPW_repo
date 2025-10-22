package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.adapter.DateTarget;
import ldg.progettoispw.engineering.adapter.SQLDateAdapter;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.engineering.applicativo.Validator;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.engineering.dao.RegistrationDAO;
import ldg.progettoispw.model.User;

import java.sql.Date;
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

    private final DateTarget dateAdapter = new SQLDateAdapter();

    public int registerUser(UserBean bean) {
        int result = validateInput(bean);
        if (result != OK) return result;

        try {
            // ✅ Conversione Bean → Model (usando l’Adapter)
            Date sqlDate = dateAdapter.convert(bean.getBirthDate());
            User user = new User(
                    bean.getName(),
                    bean.getSurname(),
                    sqlDate,
                    bean.getEmail(),
                    bean.getPassword(),
                    bean.getRole()
            );

            RegistrationDAO dao = new RegistrationDAO();
            int exists = dao.checkInDB(user);
            if (exists == 1) return USER_EXISTS;

            // Inserimento materie
            String[] subjects = bean.getSubjects().split(",");
            for (String subject : subjects) {
                String trimmed = subject.trim();
                dao.insertSubject(trimmed);
                dao.createAssociation(user.getEmail(), trimmed);
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