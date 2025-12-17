package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.api.SentimentAnalyzer;
import ldg.progettoispw.engineering.applicativo.LoginSessionManager;
import ldg.progettoispw.engineering.applicativo.ReviewFilter;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.bean.RecensioneBean;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.engineering.dao.AppointmentDAO;
import ldg.progettoispw.engineering.dao.RecensioneDAO;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.engineering.exception.SentimentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AppRispostiStudenteCtrlApplicativoTest {

    private AppRispostiStudenteCtrlApplicativo controller;

    // Definiamo i Mock per le dipendenze che sono campi della classe
    @Mock private ReviewFilter mockReviewFilter;
    @Mock private SentimentAnalyzer mockSentimentAnalyzer;
    @Mock private RecensioneDAO mockRecensioneDAO;

    @BeforeEach
    void setUp() throws Exception {
        // Inizializza i mock annotati con @Mock
        MockitoAnnotations.openMocks(this);

        // Istanzia il controller reale
        controller = new AppRispostiStudenteCtrlApplicativo();

        // --- REFLECTION INJECTION ---
        // Poiché i campi nella classe sono 'private final' e inizializzati con 'new',
        // non possiamo passarli nel costruttore. Usiamo la Reflection per forzarli.
        injectMock("reviewChecker", mockReviewFilter);
        injectMock("sentimentAnalyzer", mockSentimentAnalyzer);
        injectMock("recensioneDAO", mockRecensioneDAO);
    }

    /**
     * Metodo helper per iniettare oggetti mock nei campi privati/finali
     */
    private void injectMock(String fieldName, Object mock) throws Exception {
        Field field = AppRispostiStudenteCtrlApplicativo.class.getDeclaredField(fieldName);
        field.setAccessible(true); // Rimuove la protezione private
        field.set(controller, mock); // Imposta il nostro mock al posto dell'oggetto vero
    }

    // =================================================================================
    // TEST METODO: getAppuntamentiStudente
    // Gestisce: LoginSessionManager (Statico) e AppointmentDAO (Locale/Costruttore)
    // =================================================================================

    @Test
    void testGetAppuntamentiStudente_Success() throws DBException {
        // 1. Setup Dati
        UserBean mockUser = new UserBean();
        mockUser.setEmail("studente@test.com");
        List<AppointmentBean> expectedList = new ArrayList<>();
        expectedList.add(new AppointmentBean()); // Aggiungiamo un elemento dummy

        // 2. Mocking Avanzato
        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class);
             MockedConstruction<AppointmentDAO> daoMock = mockConstruction(AppointmentDAO.class,
                     (mock, context) -> {
                         // Istruiamo il mock creato dentro il metodo
                         when(mock.getAppuntamentiByEmail("studente@test.com", 0)).thenReturn(expectedList);
                     })) {

            // Simuliamo la sessione attiva
            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(mockUser);

            // 3. Esecuzione
            List<AppointmentBean> result = controller.getAppuntamentiStudente();

            // 4. Verifica
            assertNotNull(result);
            assertEquals(1, result.size());
            assertSame(expectedList, result);

            // ✅ CORREZIONE: Usiamo la variabile per verificare che il costruttore sia stato chiamato 1 volta.
            assertEquals(1, daoMock.constructed().size());
        }
    }

    @Test
    void testGetAppuntamentiStudente_NoSession() {
        // Testiamo il caso in cui l'utente non è loggato
        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class)) {
            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(null);

            // Ci aspettiamo un'eccezione IllegalStateException
            IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                    controller.getAppuntamentiStudente()
            );
            assertEquals("Sessione studente non attiva o email mancante.", ex.getMessage());
        }
    }

    // =================================================================================
    // TEST METODO: inviaRecensione
    // Gestisce: ReviewFilter, SentimentAnalyzer, RecensioneDAO (Iniettati via Reflection)
    // =================================================================================

    @Test
    void testInviaRecensione_Success() throws DBException, SentimentException {
        // 1. Setup Bean
        RecensioneBean bean = new RecensioneBean();
        bean.setRecensione("Tutor eccellente!");

        // 2. Definizione Comportamento Mock
        when(mockReviewFilter.containsBadWords(anyString())).thenReturn(false); // No parolacce
        when(mockSentimentAnalyzer.analyze(anyString())).thenReturn(5); // Sentiment 5 stelle
        doNothing().when(mockRecensioneDAO).insertRecensione(bean); // Salvataggio OK

        // 3. Esecuzione
        String result = controller.inviaRecensione(bean);

        // 4. Verifica
        assertEquals("Recensione inviata correttamente.", result);
        assertEquals(5, bean.getSentimentValue()); // Verifica che il sentiment sia stato settato

        // Verifica che il metodo insert sia stato chiamato 1 volta
        verify(mockRecensioneDAO, times(1)).insertRecensione(bean);
    }

    @Test
    void testInviaRecensione_BadWords() throws DBException {
        // Setup: recensione con parolacce
        RecensioneBean bean = new RecensioneBean();
        bean.setRecensione("Parolaccia!");

        // Simuliamo che il filtro trovi parolacce
        when(mockReviewFilter.containsBadWords(anyString())).thenReturn(true);

        // Esecuzione
        String result = controller.inviaRecensione(bean);

        // Verifica
        assertTrue(result.startsWith("Errore:")); // Deve restituire messaggio di errore

        // FONDAMENTALE: Verifica che NON abbia chiamato il DB né l'analizzatore sentiment
        verify(mockRecensioneDAO, never()).insertRecensione(any());
        verifyNoInteractions(mockSentimentAnalyzer);
    }

    @Test
    void testInviaRecensione_DBException() throws DBException, SentimentException {
        // SCENARIO: Il database fallisce durante l'inserimento
        RecensioneBean bean = new RecensioneBean();
        bean.setRecensione("Tutor bravo");

        // Mock: tutto ok tranne il DAO che lancia eccezione
        when(mockReviewFilter.containsBadWords(anyString())).thenReturn(false);
        when(mockSentimentAnalyzer.analyze(anyString())).thenReturn(4);

        // Simuliamo l'errore del DB (Checked Exception)
        doThrow(new DBException("Connessione persa")).when(mockRecensioneDAO).insertRecensione(bean);

        // Esecuzione
        String result = controller.inviaRecensione(bean);

        // Verifica
        // Il controller cattura l'eccezione e restituisce una stringa che contiene l'errore
        assertTrue(result.contains("Errore durante l'analisi"));
        assertTrue(result.contains("Connessione persa"));

        // Verifichiamo che il tentativo di salvataggio sia stato fatto
        verify(mockRecensioneDAO).insertRecensione(bean);
    }

    @Test
    void testInviaRecensione_GenericException() throws SentimentException {
        // SCENARIO: Errore generico (es. API Sentiment offline)
        RecensioneBean bean = new RecensioneBean();
        bean.setRecensione("Test");

        when(mockReviewFilter.containsBadWords(anyString())).thenReturn(false);
        // L'analizzatore lancia RuntimeException
        when(mockSentimentAnalyzer.analyze(anyString())).thenThrow(new RuntimeException("API Offline"));

        String result = controller.inviaRecensione(bean);

        assertTrue(result.contains("API Offline"));
        // Il DB non deve essere chiamato se fallisce l'analisi prima
        try {
            verify(mockRecensioneDAO, never()).insertRecensione(any());
        } catch (DBException _) {
            fail("Non dovrebbe lanciare eccezioni qui");
        }
    }
}