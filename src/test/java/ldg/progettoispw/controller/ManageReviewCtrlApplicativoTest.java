package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.applicativo.LoginSessionManager;
import ldg.progettoispw.engineering.bean.RecensioneBean;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.engineering.dao.RecensioneDAO;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.model.Recensione;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ManageReviewCtrlApplicativoTest {

    private ManageReviewCtrlApplicativo controller;

    @Mock
    private RecensioneDAO mockDao;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = new ManageReviewCtrlApplicativo();

        // --- REFLECTION INJECTION ---
        // Sostituiamo il DAO privato "recensioneDAO" con il mock
        Field field = ManageReviewCtrlApplicativo.class.getDeclaredField("recensioneDAO");
        field.setAccessible(true);
        field.set(controller, mockDao);
    }

    // =================================================================================
    // TEST: getRecensioniEValoriSentiment
    // Verifica: Sessione, Chiamata DAO, Mapping Model -> Bean, Wrapper Result
    // =================================================================================

    @Test
    void testGetRecensioniEValoriSentiment_Success() throws DBException {
        // 1. SETUP DATI
        UserBean mockUser = new UserBean();
        mockUser.setEmail("tutor@test.com");

        // Creiamo una lista di Model (quello che restituisce il DB)
        List<Recensione> mockModels = new ArrayList<>();
        // Recensione 1
        Recensione r1 = new Recensione();
        r1.setEmailStudente("studente1@test.com");
        r1.setTesto("Bravo tutor");
        r1.setSentimentScore(5);
        mockModels.add(r1);

        // Recensione 2
        Recensione r2 = new Recensione();
        r2.setEmailStudente("studente2@test.com");
        r2.setTesto("Non mi piace");
        r2.setSentimentScore(1);
        mockModels.add(r2);

        // 2. MOCK STATIC + DAO
        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class)) {
            // Simuliamo utente loggato
            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(mockUser);

            // Simuliamo risposta del DAO
            when(mockDao.getRecensioniByTutor("tutor@test.com")).thenReturn(mockModels);

            // 3. ESECUZIONE
            ManageReviewCtrlApplicativo.RecensioniResult result = controller.getRecensioniEValoriSentiment();

            // 4. VERIFICA
            assertNotNull(result);

            // Verifica Lista Beans
            List<RecensioneBean> beans = result.getRecensioni();
            assertEquals(2, beans.size());
            assertEquals("studente1@test.com", beans.get(0).getStudentEmail());
            assertEquals("Bravo tutor", beans.get(0).getRecensione());
            assertEquals(5, beans.get(0).getSentimentValue()); // Controlliamo che il mapping sia corretto

            // Verifica Lista Interi (Sentiment Values)
            List<Integer> values = result.getSentimentValues();
            assertEquals(2, values.size());
            assertEquals(5, values.get(0));
            assertEquals(1, values.get(1));

            // Verifica chiamata al DAO
            verify(mockDao).getRecensioniByTutor("tutor@test.com");
        }
    }

    @Test
    void testGetRecensioniEValoriSentiment_NoSession() {
        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class)) {
            // Simuliamo nessun utente loggato
            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(null);

            // Assert Throws
            IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                    controller.getRecensioniEValoriSentiment()
            );
            assertEquals("Nessun tutor loggato.", ex.getMessage());
        }
    }

    @Test
    void testGetRecensioniEValoriSentiment_EmptyEmail() {
        UserBean mockUser = new UserBean();
        mockUser.setEmail(""); // Email vuota

        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class)) {
            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(mockUser);

            IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                    controller.getRecensioniEValoriSentiment()
            );
            assertEquals("Nessun tutor loggato.", ex.getMessage());
        }
    }

    @Test
    void testGetRecensioniEValoriSentiment_DBException() throws DBException {
        UserBean mockUser = new UserBean();
        mockUser.setEmail("tutor@test.com");

        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class)) {
            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(mockUser);

            // Simuliamo errore DB
            when(mockDao.getRecensioniByTutor(anyString())).thenThrow(new DBException("Errore Query"));

            // Verifica che l'eccezione risalga
            assertThrows(DBException.class, () -> controller.getRecensioniEValoriSentiment());
        }
    }

    @Test
    void testGetRecensioniEValoriSentiment_EmptyResults() throws DBException {
        // Caso in cui il tutor non ha recensioni
        UserBean mockUser = new UserBean();
        mockUser.setEmail("tutor@test.com");

        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class)) {
            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(mockUser);

            // Ritorna lista vuota
            when(mockDao.getRecensioniByTutor(anyString())).thenReturn(new ArrayList<>());

            ManageReviewCtrlApplicativo.RecensioniResult result = controller.getRecensioniEValoriSentiment();

            assertNotNull(result);
            assertTrue(result.getRecensioni().isEmpty());
            assertTrue(result.getSentimentValues().isEmpty());
        }
    }
}