package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.applicativo.DateUtils;
import ldg.progettoispw.engineering.applicativo.Validator;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.engineering.dao.RegistrationDAO;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RegistrationCtrlApplicativoTest {

    private RegistrationCtrlApplicativo controller;

    // Costanti replicate per leggibilità del test
    private static final int OK = 0;
    private static final int USER_EXISTS = 1;
    private static final int EMPTY_FIELD = 2;
    private static final int INVALID_EMAIL = 3;
    private static final int INVALID_PASSWORD = 4;
    private static final int INVALID_DATE = 5;
    private static final int DB_ERROR = 6;

    @BeforeEach
    void setUp() {
        controller = new RegistrationCtrlApplicativo();
    }

    // =================================================================================
    // TEST: Validazione Input (Casi che ritornano codici errore senza toccare il DAO)
    // =================================================================================

    @Test
    void testRegisterUser_EmptyField() {
        // Setup: Bean con campo obbligatorio vuoto
        UserBean bean = new UserBean();
        bean.setName(""); // Campo vuoto
        bean.setSurname("Rossi");
        bean.setEmail("a@b.c");
        bean.setPassword("pass");
        bean.setBirthDate("01/01/1990");
        bean.setSubjects("Math");

        // Esecuzione
        int result = controller.registerUser(bean);

        // Verifica
        assertEquals(EMPTY_FIELD, result);
    }

    @Test
    void testRegisterUser_InvalidEmail() {
        UserBean bean = createValidBean(); // Bean base valido

        // Mockiamo Validator per forzare l'errore Email
        try (MockedStatic<Validator> validatorMock = mockStatic(Validator.class)) {
            validatorMock.when(() -> Validator.isValidEmail(anyString())).thenReturn(false);

            int result = controller.registerUser(bean);

            assertEquals(INVALID_EMAIL, result);
        }
    }

    @Test
    void testRegisterUser_InvalidPassword() {
        UserBean bean = createValidBean();

        try (MockedStatic<Validator> validatorMock = mockStatic(Validator.class)) {
            validatorMock.when(() -> Validator.isValidEmail(anyString())).thenReturn(true);
            // Simuliamo password non valida
            validatorMock.when(() -> Validator.isValidPassword(anyString())).thenReturn(false);

            int result = controller.registerUser(bean);

            assertEquals(INVALID_PASSWORD, result);
        }
    }

    @Test
    void testRegisterUser_InvalidDate() {
        UserBean bean = createValidBean();

        try (MockedStatic<Validator> validatorMock = mockStatic(Validator.class)) {
            validatorMock.when(() -> Validator.isValidEmail(anyString())).thenReturn(true);
            validatorMock.when(() -> Validator.isValidPassword(anyString())).thenReturn(true);
            // Simuliamo data non valida
            validatorMock.when(() -> Validator.isValidDate(anyString())).thenReturn(false);

            int result = controller.registerUser(bean);

            assertEquals(INVALID_DATE, result);
        }
    }

    // =================================================================================
    // TEST: Logica di Business e DAO (Successo e Errori DB)
    // Richiede Mock: Validator (pass), DateUtils, RegistrationDAO
    // =================================================================================

    @Test
    void testRegisterUser_Success() throws DBException {
        UserBean bean = createValidBean();
        bean.setSubjects("Matematica, Fisica"); // Due materie per testare il loop

        // Dobbiamo mockare TUTTO: Validator, DateUtils e il costruttore DAO
        try (MockedStatic<Validator> validatorMock = mockStatic(Validator.class);
             MockedStatic<DateUtils> dateMock = mockStatic(DateUtils.class);
             MockedConstruction<RegistrationDAO> daoMock = mockConstruction(RegistrationDAO.class,
                     (mock, context) -> {
                         // Istruiamo il DAO: l'utente NON esiste (0)
                         when(mock.checkInDB(any(User.class))).thenReturn(0);
                     })) {

            // 1. Configurazione Statiche
            validatorMock.when(() -> Validator.isValidEmail(anyString())).thenReturn(true);
            validatorMock.when(() -> Validator.isValidPassword(anyString())).thenReturn(true);
            validatorMock.when(() -> Validator.isValidDate(anyString())).thenReturn(true);

            // Mock DateUtils
            Date dummyDate = new Date(System.currentTimeMillis());
            dateMock.when(() -> DateUtils.toSqlDate(anyString())).thenReturn(dummyDate);

            // 2. Esecuzione
            int result = controller.registerUser(bean);

            // 3. Verifica Risultato
            assertEquals(OK, result);

            // 4. Verifica Interazioni DAO
            RegistrationDAO createdDao = daoMock.constructed().get(0);

            // Verifica che checkInDB sia stato chiamato
            verify(createdDao).checkInDB(any(User.class));

            // Verifica che insertSubject sia stato chiamato 2 volte (Matematica, Fisica)
            verify(createdDao, times(2)).insertSubject(anyString());
            verify(createdDao).insertSubject("Matematica");
            verify(createdDao).insertSubject("Fisica");

            // Verifica associazioni
            verify(createdDao, times(2)).createAssociation(eq("test@email.com"), anyString());
        }
    }

    @Test
    void testRegisterUser_UserExists() {
        UserBean bean = createValidBean();

        try (MockedStatic<Validator> validatorMock = mockStatic(Validator.class);
             MockedStatic<DateUtils> dateMock = mockStatic(DateUtils.class);
             MockedConstruction<RegistrationDAO> daoMock = mockConstruction(RegistrationDAO.class,
                     (mock, context) -> {
                         // Simuliamo che l'utente ESISTA già (ritorna 1)
                         when(mock.checkInDB(any(User.class))).thenReturn(1);
                     })) {

            configureValidationSuccess(validatorMock);
            dateMock.when(() -> DateUtils.toSqlDate(anyString())).thenReturn(new Date(System.currentTimeMillis()));

            int result = controller.registerUser(bean);

            assertEquals(USER_EXISTS, result);

            // Se l'utente esiste, non deve provare a inserire materie
            RegistrationDAO createdDao = daoMock.constructed().get(0);
            try {
                verify(createdDao, never()).insertSubject(anyString());
            } catch (DBException e) {
                // ignore
            }
        }
    }

    @Test
    void testRegisterUser_DBException() {
        UserBean bean = createValidBean();

        try (MockedStatic<Validator> validatorMock = mockStatic(Validator.class);
             MockedStatic<DateUtils> dateMock = mockStatic(DateUtils.class);
             MockedConstruction<RegistrationDAO> daoMock = mockConstruction(RegistrationDAO.class,
                     (mock, context) -> {
                         // Simuliamo Errore DB
                         when(mock.checkInDB(any(User.class))).thenThrow(new DBException("Errore connessione"));
                     })) {

            configureValidationSuccess(validatorMock);
            dateMock.when(() -> DateUtils.toSqlDate(anyString())).thenReturn(new Date(System.currentTimeMillis()));

            int result = controller.registerUser(bean);

            assertEquals(DB_ERROR, result);
        }
    }

    // --- Metodi Helper per pulire il codice ---

    private UserBean createValidBean() {
        UserBean bean = new UserBean();
        bean.setName("Mario");
        bean.setSurname("Rossi");
        bean.setEmail("test@email.com");
        bean.setPassword("Password123!");
        bean.setBirthDate("01/01/2000");
        bean.setSubjects("Informatica");
        bean.setRole("Student");
        return bean;
    }

    private void configureValidationSuccess(MockedStatic<Validator> validatorMock) {
        validatorMock.when(() -> Validator.isValidEmail(anyString())).thenReturn(true);
        validatorMock.when(() -> Validator.isValidPassword(anyString())).thenReturn(true);
        validatorMock.when(() -> Validator.isValidDate(anyString())).thenReturn(true);
    }
}