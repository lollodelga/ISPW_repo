package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.applicativo.LoginSessionManager;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.engineering.dao.AppointmentDAO;
import ldg.progettoispw.engineering.exception.DBException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppInAttesaStudenteCtrlApplicativoTest {

    @Test
    void testGetAppuntamentiInAttesa_Success() throws DBException {
        // 1. SETUP DATI
        UserBean mockUser = new UserBean();
        mockUser.setEmail("studente@test.com");

        List<AppointmentBean> expectedList = new ArrayList<>();
        expectedList.add(new AppointmentBean()); // Aggiungiamo un elemento finto

        // 2. MOCKING AVANZATO (Static + Constructor)

        // Mockiamo la classe statica LoginSessionManager
        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class);
             // Mockiamo il costruttore di AppointmentDAO per intercettare il "new"
             MockedConstruction<AppointmentDAO> daoMock = mockConstruction(AppointmentDAO.class,
                     (mock, context) -> {
                         // Diciamo al mock cosa restituire quando viene chiamato il metodo
                         when(mock.getAppuntamentiInAttesa("studente@test.com", false)).thenReturn(expectedList);
                     })) {

            // Configuriamo la sessione statica
            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(mockUser);

            // 3. ESECUZIONE
            AppInAttesaStudenteCtrlApplicativo controller = new AppInAttesaStudenteCtrlApplicativo();
            List<AppointmentBean> result = controller.getAppuntamentiInAttesa();

            // 4. VERIFICA
            assertNotNull(result);
            assertEquals(1, result.size());
            assertSame(expectedList, result); // Verifichiamo che sia esattamente la lista restituita dal mock

            // Verifichiamo che il DAO sia stato istanziato una volta
            assertEquals(1, daoMock.constructed().size());
        }
    }

    @Test
    void testGetAppuntamentiInAttesa_SessionNull() {
        // SCENARIO: Utente non loggato (sessione null)

        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class)) {
            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(null);

            AppInAttesaStudenteCtrlApplicativo controller = new AppInAttesaStudenteCtrlApplicativo();

            // Ci aspettiamo un'eccezione
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                controller.getAppuntamentiInAttesa();
            });

            assertEquals("Sessione utente non attiva.", exception.getMessage());
        }
    }

    @Test
    void testGetAppuntamentiInAttesa_EmailNull() {
        // SCENARIO: Utente loggato ma senza email (caso limite)
        UserBean mockUser = new UserBean();
        mockUser.setEmail(null); // Email mancante

        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class)) {
            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(mockUser);

            AppInAttesaStudenteCtrlApplicativo controller = new AppInAttesaStudenteCtrlApplicativo();

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                controller.getAppuntamentiInAttesa();
            });

            assertEquals("Email del tutor non trovata nella sessione.", exception.getMessage());
        }
    }

    @Test
    void testGetAppuntamentiInAttesa_DBException() {
        // SCENARIO: Il DAO lancia un'eccezione DB
        UserBean mockUser = new UserBean();
        mockUser.setEmail("studente@test.com");

        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class);
             MockedConstruction<AppointmentDAO> daoMock = mockConstruction(AppointmentDAO.class,
                     (mock, context) -> {
                         // Simuliamo errore DB
                         when(mock.getAppuntamentiInAttesa(anyString(), anyBoolean()))
                                 .thenThrow(new DBException("Errore di connessione"));
                     })) {

            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(mockUser);

            AppInAttesaStudenteCtrlApplicativo controller = new AppInAttesaStudenteCtrlApplicativo();

            // Verifichiamo che l'eccezione risalga fino al controller
            DBException exception = assertThrows(DBException.class, () -> {
                controller.getAppuntamentiInAttesa();
            });

            assertEquals("Errore di connessione", exception.getMessage());

            // ✅ CORREZIONE: Usiamo la variabile per verificare che il DAO sia stato creato
            assertEquals(1, daoMock.constructed().size());
        }
    }
}