package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.api.PythonServerLauncher;
import ldg.progettoispw.engineering.applicativo.LoginSessionManager;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.engineering.dao.LoginDAO;
import ldg.progettoispw.engineering.dao.UserDAO;
import ldg.progettoispw.engineering.exception.IncorrectPasswordException;
import ldg.progettoispw.engineering.exception.InvalidEmailException;
import ldg.progettoispw.engineering.exception.UserDoesNotExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginCtrlApplicativoTest {

    private LoginCtrlApplicativo controller;

    // Mock dei DAO privati
    @Mock private LoginDAO mockLoginDAO;
    @Mock private UserDAO mockUserDAO;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = new LoginCtrlApplicativo();

        // REFLECTION INJECTION
        injectMock("loginDAO", mockLoginDAO);
        injectMock("userDAO", mockUserDAO);
    }

    private void injectMock(String fieldName, Object mock) throws Exception {
        Field field = LoginCtrlApplicativo.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, mock);
    }

    // =================================================================================
    // TEST: Validazione Input (Email/Password vuote o formato errato)
    // Non serve mockare il DB qui, perché l'errore avviene prima.
    // =================================================================================

    @Test
    void testVerificaCredenziali_EmptyFields() {
        assertThrows(InvalidEmailException.class, () ->
                controller.verificaCredenziali("", "")
        );
    }

    @Test
    void testVerificaCredenziali_NullFields() {
        assertThrows(InvalidEmailException.class, () ->
                controller.verificaCredenziali(null, "password")
        );
    }

    @Test
    void testVerificaCredenziali_InvalidEmailFormat() {
        assertThrows(InvalidEmailException.class, () ->
                controller.verificaCredenziali("email_sbagliata_senza_chiocciola", "password123")
        );
    }

    // =================================================================================
    // TEST: Flusso di Successo (Login Corretto)
    // Richiede: Mock LoginDAO, UserDAO, LoginSessionManager e PythonServerLauncher
    // =================================================================================

    @Test
    void testVerificaCredenziali_Success_Tutor() throws Exception {
        // 1. Dati di input
        String email = "tutor@test.com";
        String password = "passwordSegreta";

        // Dati che restituisce il UserDAO
        String[] mockUserData = {"Nome", "Cognome", email, "password", "1990-01-01", null};
        String mockSubjects = "Matematica";

        // 2. Mock Multipli (Sessione + Python Launcher)
        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class);
             MockedStatic<PythonServerLauncher> pythonMock = mockStatic(PythonServerLauncher.class)) {

            // Configurazione DAO
            // Simuliamo che start() ritorni SUCCESS (costante presa dalla classe o valore int atteso)
            // Assumiamo che LoginDAO.SUCCESS sia accessibile o mappiamo il valore corretto
            // (Se LoginDAO.SUCCESS è una costante statica pubblica, usala. Altrimenti metti il valore int corretto)
            when(mockLoginDAO.start(email, password)).thenReturn(LoginDAO.SUCCESS);

            // Configuriamo recupero dati utente
            when(mockUserDAO.takeData(email, password)).thenReturn(mockUserData);
            when(mockUserDAO.takeSubjects(email)).thenReturn(mockSubjects);

            // Configuriamo il ruolo (1 = Tutor)
            when(mockLoginDAO.getUserRole(email, password)).thenReturn(1);

            // 3. ESECUZIONE
            int ruolo = controller.verificaCredenziali(email, password);

            // 4. VERIFICA
            assertEquals(1, ruolo); // Deve ritornare ruolo Tutor

            // Verifiche sulle chiamate statiche
            sessionMock.verify(() -> LoginSessionManager.saveUserSession(any(UserBean.class)), times(1));
            pythonMock.verify(PythonServerLauncher::launch, times(1)); // Importante: Verifica che Python venga lanciato
        }
    }

    // =================================================================================
    // TEST: Casi di Errore dal Database (Password Errata, Utente non trovato)
    // =================================================================================

    @Test
    void testVerificaCredenziali_WrongPassword() throws Exception {
        String email = "student@test.com";
        String password = "passwordSbagliata";

        // Simuliamo risposta WRONG_PASSWORD dal DAO
        when(mockLoginDAO.start(email, password)).thenReturn(LoginDAO.WRONG_PASSWORD);

        // Ci aspettiamo IncorrectPasswordException
        IncorrectPasswordException ex = assertThrows(IncorrectPasswordException.class, () ->
                controller.verificaCredenziali(email, password)
        );
        assertEquals("Password errata.", ex.getMessage());
    }

    @Test
    void testVerificaCredenziali_UserNotFound() throws Exception {
        String email = "non_esiste@test.com";
        String password = "password";

        // Simuliamo risposta USER_NOT_FOUND dal DAO
        when(mockLoginDAO.start(email, password)).thenReturn(LoginDAO.USER_NOT_FOUND);

        // Ci aspettiamo UserDoesNotExistException
        UserDoesNotExistException ex = assertThrows(UserDoesNotExistException.class, () ->
                controller.verificaCredenziali(email, password)
        );
        assertEquals("Utente non trovato.", ex.getMessage());
    }
}