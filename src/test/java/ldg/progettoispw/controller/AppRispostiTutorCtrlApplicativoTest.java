package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.applicativo.LoginSessionManager;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.engineering.dao.AppointmentDAO;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.engineering.gof.state.AppointmentContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppRispostiTutorCtrlApplicativoTest {

    private AppRispostiTutorCtrlApplicativo controller;

    @Mock
    private AppointmentDAO mockDao;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = new AppRispostiTutorCtrlApplicativo();

        // --- REFLECTION INJECTION ---
        // Sostituiamo il DAO reale (creato nel campo) con il nostro Mock
        Field daoField = AppRispostiTutorCtrlApplicativo.class.getDeclaredField("dao");
        daoField.setAccessible(true);
        daoField.set(controller, mockDao);
    }

    // =================================================================================
    // TEST: getAppuntamentiTutor
    // =================================================================================

    @Test
    void testGetAppuntamentiTutor_Success() throws DBException {
        // 1. Setup Sessione
        UserBean mockUser = new UserBean();
        mockUser.setEmail("tutor@test.com");

        List<AppointmentBean> expectedList = new ArrayList<>();
        expectedList.add(new AppointmentBean());

        // 2. Mocking Statico (LoginSessionManager)
        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class)) {
            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(mockUser);

            // Configuro il DAO mockato
            when(mockDao.getAppuntamentiByEmail("tutor@test.com", 1)).thenReturn(expectedList);

            // 3. Esecuzione
            List<AppointmentBean> result = controller.getAppuntamentiTutor();

            // 4. Verifica
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(mockDao).getAppuntamentiByEmail("tutor@test.com", 1);
        }
    }

    @Test
    void testGetAppuntamentiTutor_NoSession() {
        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class)) {
            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(null);

            assertThrows(IllegalStateException.class, () -> controller.getAppuntamentiTutor());
        }
    }

    // =================================================================================
    // TEST: updateAppointmentStatus
    // Sfida: Intercettare 'new AppointmentContext()'
    // =================================================================================

    @Test
    void testUpdateAppointmentStatus_Complete() {
        UserBean mockUser = new UserBean();
        mockUser.setEmail("tutor@test.com");

        // Parametri dummy
        Date date = Date.valueOf("2024-01-01");
        Time time = Time.valueOf("10:00:00");

        // Mockiamo la Sessione E la Costruzione di AppointmentContext
        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class);
             MockedConstruction<AppointmentContext> contextMock = mockConstruction(AppointmentContext.class)) {

            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(mockUser);

            // ESECUZIONE (Azione: complete)
            controller.updateAppointmentStatus("student@test.com", date, time, "confermato", "complete");

            // VERIFICA
            // 1. Verifichiamo che AppointmentContext sia stato creato 1 volta
            assertEquals(1, contextMock.constructed().size());

            // 2. Recuperiamo l'istanza mock creata
            AppointmentContext mockCreatedContext = contextMock.constructed().get(0);

            // 3. Verifichiamo che sia stato chiamato il metodo .complete() su quell'istanza
            try {
                verify(mockCreatedContext).complete();
                verify(mockCreatedContext, never()).cancel(); // Assicuriamoci che non abbia chiamato cancel
            } catch (DBException e) {
                fail("Non dovrebbe lanciare eccezioni nei verify");
            }
        }
    }

    @Test
    void testUpdateAppointmentStatus_Cancel() {
        UserBean mockUser = new UserBean();
        mockUser.setEmail("tutor@test.com");

        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class);
             MockedConstruction<AppointmentContext> contextMock = mockConstruction(AppointmentContext.class)) {

            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(mockUser);

            // ESECUZIONE (Azione: cancel)
            controller.updateAppointmentStatus("student@test.com", null, null, "confermato", "cancel");

            AppointmentContext mockCreatedContext = contextMock.constructed().get(0);

            try {
                verify(mockCreatedContext).cancel(); // Deve chiamare cancel
                verify(mockCreatedContext, never()).complete();
            } catch (DBException e) {
                fail("Non dovrebbe lanciare eccezioni");
            }
        }
    }

    @Test
    void testUpdateAppointmentStatus_UnknownAction() {
        UserBean mockUser = new UserBean();
        mockUser.setEmail("tutor@test.com");

        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class);
             MockedConstruction<AppointmentContext> contextMock = mockConstruction(AppointmentContext.class)) {

            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(mockUser);

            // ESECUZIONE (Azione: garbage)
            controller.updateAppointmentStatus("student@test.com", null, null, "confermato", "azione_sbagliata");

            AppointmentContext mockCreatedContext = contextMock.constructed().get(0);

            // VERIFICA: Nessun metodo di stato deve essere chiamato
            try {
                verify(mockCreatedContext, never()).complete();
                verify(mockCreatedContext, never()).cancel();
            } catch (DBException e) {
                fail("Non dovrebbe lanciare eccezioni");
            }
        }
    }

    @Test
    void testUpdateAppointmentStatus_DBException() throws DBException {
        // SCENARIO: Il context lancia DBException durante il cambio di stato.
        // Il controller ha un try-catch che logga l'errore. Vogliamo assicurarci che non crashi.

        UserBean mockUser = new UserBean();
        mockUser.setEmail("tutor@test.com");

        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class);
             MockedConstruction<AppointmentContext> contextMock = mockConstruction(AppointmentContext.class,
                     (mock, context) -> {
                         // Istruiamo il mock a lanciare l'eccezione quando viene chiamato complete()
                         doThrow(new DBException("Errore DB simulato")).when(mock).complete();
                     })) {

            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(mockUser);

            // ESECUZIONE
            // Non usiamo assertThrows perché il controller CATTURA l'eccezione internamente
            controller.updateAppointmentStatus("student@test.com", null, null, "confermato", "complete");

            // Se arriviamo qui senza crashare, il test è passato.
            // Verifichiamo comunque che il metodo sia stato chiamato
            verify(contextMock.constructed().get(0)).complete();
        }
    }
}