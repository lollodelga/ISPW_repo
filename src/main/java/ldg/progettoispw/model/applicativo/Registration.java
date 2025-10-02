package ldg.progettoispw.model.applicativo;

import javafx.event.ActionEvent;
import ldg.progettoispw.exception.DBException;
import ldg.progettoispw.model.dao.RegistrationDAO;
import ldg.progettoispw.util.GController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class Registration {
    private static final Logger logger = Logger.getLogger(Registration.class.getName());

    // Codici di errore/risultato
    private static final int OK = 0;
    private static final int USER_EXISTS = 1;
    private static final int EMPTY_FIELD = 2;
    private static final int INVALID_EMAIL = 3;
    private static final int INVALID_PASSWORD = 4;
    private static final int INVALID_DATE = 5;
    private static final int DB_ERROR = 6;

    private final GController gController;
    private final ActionEvent event;

    public Registration(GController gController, ActionEvent event) {
        this.gController = gController;
        this.event = event;
    }

    public void start(String[] inputData) {
        int result = validateInput(inputData);

        try {
            if (result == OK) {
                String email = inputData[3];
                RegistrationDAO dao = new RegistrationDAO();

                if (dao.checkInDB(inputData) == 1) {
                    result = USER_EXISTS;
                } else {
                    String[] subjects = inputData[5].split(",");
                    for (String subject : subjects) {
                        String trimmed = subject.trim();
                        dao.insertSubject(trimmed);
                        dao.createAssociation(email, trimmed);
                    }
                }
            }

        } catch (DBException e) {
            logger.warning("Errore durante la registrazione: " + e.getMessage());
            result = DB_ERROR;
        }

        gController.changeView(result, event);
    }

    private int validateInput(String[] data) {
        if (hasEmptyFields(data)) return EMPTY_FIELD;
        if (!isValidEmail(data[3])) return INVALID_EMAIL;
        if (!isValidPassword(data[4])) return INVALID_PASSWORD;
        if (!isValidDate(data[2])) return INVALID_DATE;
        return OK;
    }

    private boolean hasEmptyFields(String[] data) {
        for (int i = 0; i < 6; i++) {
            if (data[i] == null || data[i].trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,7}$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[:!?$%&;])[A-Za-z\\d:!?$%&;]{8,16}$";
        return Pattern.matches(passwordRegex, password);
    }

    private boolean isValidDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            Date date = dateFormat.parse(dateStr);
            return !date.after(new Date());
        } catch (ParseException e) {
            return false;
        }
    }
}