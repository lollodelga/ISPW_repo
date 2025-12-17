package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.applicativo.LoginSessionManager;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.engineering.dao.AppointmentDAO;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.engineering.gof.state.AppointmentContext;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ManageAppointmentCtrlApplicativoTest {

    private final ManageAppointmentCtrlApplicativo controller = new ManageAppointmentCtrlApplicativo();

    // =================================================================================
    // TEST: getAppuntamentiInAttesa
    // Sfida: Intercettare 'new AppointmentDAO()' + 'LoginSessionManager' statico
    // =================================================================================

    @Test
    void testGetAppuntamentiInAttesa_Success() throws DBException {
        // 1. Setup Dati
        UserBean mockUser = new UserBean();
        mockUser.setEmail("tutor@test.com");

        List<AppointmentBean> expectedList = new ArrayList<>();
        expectedList.add(new AppointmentBean()); // Aggiungiamo un bean dummy

        // 2. Mock Combinato: Statico (Sessione) + Costruttore (DAO)
        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class);
             MockedConstruction<AppointmentDAO> daoMock = mockConstruction(AppointmentDAO.class,
                     (mock, context) -> {
                         // Istruiamo il mock del DAO creato internamente
                         // Nota: nel codice originale passi 'true' come secondo parametro
                         when(mock.getAppuntamentiInAttesa("tutor@test.com", true)).thenReturn(expectedList);
                     })) {

            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(mockUser);

            // 3. Esecuzione
            List<AppointmentBean> result = controller.getAppuntamentiInAttesa();

            // 4. Verifica
            assertNotNull(result);
            assertEquals(1, result.size());
            assertSame(expectedList, result);

            // Verifichiamo che il costruttore sia stato chiamato 1 volta
            assertEquals(1, daoMock.constructed().size());
        }
    }

    @Test
    void testGetAppuntamentiInAttesa_NoSession() {
        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class)) {
            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(null);

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    controller::getAppuntamentiInAttesa
            );
            assertEquals("Sessione utente non attiva.", ex.getMessage());
        }
    }

    @Test
    void testGetAppuntamentiInAttesa_NoEmail() {
        UserBean mockUser = new UserBean();
        mockUser.setEmail(""); // Email vuota

        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class)) {
            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(mockUser);

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    controller::getAppuntamentiInAttesa
            );
            assertEquals("Email del tutor non trovata nella sessione.", ex.getMessage());
        }
    }

    // =================================================================================
    // TEST: handleAppointmentAction (e wrapper conferma/rifiuta)
    // Sfida: Intercettare 'new AppointmentContext()' del Pattern State
    // =================================================================================

    @Test
    void testConfermaAppuntamento_Success() throws DBException {
        // 1. Setup Bean
        AppointmentBean bean = createDummyBean();

        // 2. Mock del Costruttore di AppointmentContext
        // Questo è fondamentale: quando il controller fa 'new AppointmentContext(model)',
        // Mockito intercetta e restituisce un nostro mock controllato.
        try (MockedConstruction<AppointmentContext> contextMock = mockConstruction(AppointmentContext.class)) {

            // 3. Esecuzione (chiamiamo il wrapper)
            controller.confermaAppuntamento(bean);

            // 4. Verifica
            // Recuperiamo l'istanza mockata che è stata creata dentro il metodo
            AppointmentContext mockCreatedContext = contextMock.constructed().get(0);

            // Verifichiamo che sia stato chiamato il metodo .confirm()
            verify(mockCreatedContext).confirm();
            // Verifichiamo che NON sia stato chiamato .cancel()
            verify(mockCreatedContext, never()).cancel();
        }
    }

    @Test
    void testRifiutaAppuntamento_Success() throws DBException {
        AppointmentBean bean = createDummyBean();

        try (MockedConstruction<AppointmentContext> contextMock = mockConstruction(AppointmentContext.class)) {

            // 3. Esecuzione (chiamiamo il wrapper)
            controller.rifiutaAppuntamento(bean);

            // 4. Verifica
            AppointmentContext mockCreatedContext = contextMock.constructed().get(0);

            // Verifichiamo che sia stato chiamato il metodo .cancel()
            verify(mockCreatedContext).cancel();
            verify(mockCreatedContext, never()).confirm();
        }
    }

    @Test
    void testHandleAppointmentAction_InvalidAction() {
        AppointmentBean bean = createDummyBean();

        // Anche se lancia eccezione dopo, mockiamo il context per isolare il test
        try (MockedConstruction<AppointmentContext> contextMock = mockConstruction(AppointmentContext.class)) {

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                    controller.handleAppointmentAction(bean, "azione_inventata")
            );

            assertEquals("Azione non valida: azione_inventata", ex.getMessage());

            // ✅ CORREZIONE: Verifichiamo che il Context sia stato comunque istanziato
            // (perché nel codice il 'new' avviene PRIMA del controllo sullo switch)
            assertEquals(1, contextMock.constructed().size());
        }
    }

    // --- Helper per creare dati dummy ---
    private AppointmentBean createDummyBean() {
        AppointmentBean bean = new AppointmentBean();
        bean.setId(1);
        bean.setStudenteEmail("studente@test.com");
        bean.setTutorEmail("tutor@test.com");
        bean.setData(Date.valueOf("2025-01-01"));
        bean.setOra(Time.valueOf("10:00:00"));
        bean.setStato("IN_ATTESA");
        return bean;
    }
}