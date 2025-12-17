package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.applicativo.LoginSessionManager;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.util.GControllerHome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class HomePageControllerTest {

    private HomePageController controller;

    // Mock dell'interfaccia grafica (la View)
    @Mock
    private GControllerHome mockView;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new HomePageController();
    }

    // =================================================================================
    // TEST: refreshUserData
    // Verifica che i dati vengano presi dalla Sessione e passati alla View
    // =================================================================================

    @Test
    void testRefreshUserData_Success() {
        // 1. Setup Dati Utente Fittizi
        UserBean mockUser = new UserBean();
        mockUser.setName("Mario");
        mockUser.setSurname("Rossi");
        mockUser.setBirthDate("1990-01-01");
        mockUser.setSubjects("Matematica, Fisica"); // Assumendo che sia una stringa nel bean

        // 2. Mock Statico della Sessione
        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class)) {
            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(mockUser);

            // 3. Esecuzione
            controller.refreshUserData(mockView);

            // 4. Verifica
            // Controlliamo che il metodo updateUserData della view sia stato chiamato
            // ESATTAMENTE con i dati presi dal bean.
            verify(mockView).updateUserData(
                    "Mario",
                    "Rossi",
                    "1990-01-01",
                    "Matematica, Fisica"
            );
        }
    }

    @Test
    void testRefreshUserData_NoSession() {
        // SCENARIO: Utente non loggato (loadUserSession ritorna null)

        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class)) {
            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(null);

            // Esecuzione
            controller.refreshUserData(mockView);

            // Verifica
            // Importante: Assicuriamoci che la view NON venga aggiornata se non c'è utente
            verifyNoInteractions(mockView);
        }
    }

    @Test
    void testRefreshUserData_Exception() {
        // SCENARIO: Errore imprevisto durante il caricamento (es. errore di parsing)

        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class)) {
            // Simuliamo un'eccezione
            sessionMock.when(LoginSessionManager::loadUserSession).thenThrow(new RuntimeException("Errore generico"));

            // Esecuzione
            // Il controller ha un try-catch interno, quindi NON ci aspettiamo che il test crashi
            controller.refreshUserData(mockView);

            // Verifica
            // La view non deve essere stata toccata
            verifyNoInteractions(mockView);
        }
    }

    // =================================================================================
    // TEST: logout
    // =================================================================================

    @Test
    void testLogout() {
        // Dobbiamo verificare che venga chiamato il metodo statico clearSession()

        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class)) {

            // Esecuzione
            controller.logout();

            // Verifica su metodo statico void
            sessionMock.verify(LoginSessionManager::clearSession, times(1));
        }
    }
}