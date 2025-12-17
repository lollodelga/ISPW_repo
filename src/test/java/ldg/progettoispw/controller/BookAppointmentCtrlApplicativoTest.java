package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.applicativo.LoginSessionManager;
import ldg.progettoispw.engineering.bean.SubjectBean;
import ldg.progettoispw.engineering.bean.TutorBean;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.engineering.dao.AppointmentDAO;
import ldg.progettoispw.engineering.dao.TutorSearchDAO;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.model.Tutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookAppointmentCtrlApplicativoTest {

    private BookAppointmentCtrlApplicativo controller;

    // Mock dei DAO (che sono campi privati nel controller)
    @Mock private TutorSearchDAO mockTutorDAO;
    @Mock private AppointmentDAO mockAppointmentDAO;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = new BookAppointmentCtrlApplicativo();

        // --- REFLECTION INJECTION ---
        // Sostituiamo i DAO reali con i Mock
        injectMock("tutorDAO", mockTutorDAO);
        injectMock("dao", mockAppointmentDAO);
    }

    private void injectMock(String fieldName, Object mock) throws Exception {
        Field field = BookAppointmentCtrlApplicativo.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, mock);
    }

    // =================================================================================
    // TEST: searchTutorBySubject
    // Verifica che il controller chiami il DAO e converta correttamente Model -> Bean
    // =================================================================================

    @Test
    void testSearchTutorBySubject_Success() throws DBException {
        // 1. Setup
        SubjectBean subjectBean = new SubjectBean("Matematica");

        // Prepariamo la lista di Model che il DAO restituirebbe
        List<Tutor> mockModels = new ArrayList<>();
        Tutor t1 = new Tutor("tutor@test.com", "Mario", "Rossi");
        t1.addMateria("Matematica");
        mockModels.add(t1);

        // 2. Mock Behavior
        when(mockTutorDAO.findTutorsBySubject("Matematica")).thenReturn(mockModels);

        // 3. Esecuzione
        List<TutorBean> result = controller.searchTutorBySubject(subjectBean);

        // 4. Verifica
        assertNotNull(result);
        assertEquals(1, result.size());

        // Verifica mappatura campi
        TutorBean resultBean = result.get(0);
        assertEquals("Mario", resultBean.getNome());
        assertEquals("Rossi", resultBean.getCognome());
        assertEquals("tutor@test.com", resultBean.getEmail());
        assertTrue(resultBean.getMaterie().contains("Matematica"));
    }

    @Test
    void testSearchTutorBySubject_Empty() throws DBException {
        // Caso: Nessun tutor trovato
        SubjectBean subjectBean = new SubjectBean("Fisica");
        when(mockTutorDAO.findTutorsBySubject("Fisica")).thenReturn(new ArrayList<>());

        List<TutorBean> result = controller.searchTutorBySubject(subjectBean);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // =================================================================================
    // TEST: bookAppointment
    // Verifica validazioni (Data passata, ora errata) e inserimento nel DB
    // =================================================================================

    @Test
    void testBookAppointment_Success() throws DBException {
        // 1. Setup
        TutorBean tutorBean = new TutorBean();
        tutorBean.setEmail("tutor@test.com");

        UserBean studentUser = new UserBean();
        studentUser.setEmail("student@test.com");

        // Data futura per evitare errore "data passata"
        LocalDate futureDate = LocalDate.now().plusDays(5);
        int validHour = 15;

        // 2. Mock Statico Sessione
        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class)) {
            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(studentUser);

            // 3. Esecuzione
            controller.bookAppointment(tutorBean, futureDate, validHour);

            // 4. Verifica
            // Verifichiamo che il DAO sia stato chiamato con i tipi convertiti (Date SQL e Time SQL)
            verify(mockAppointmentDAO).insertAppointment(
                    eq("student@test.com"),
                    eq("tutor@test.com"),
                    any(Date.class), // Controlliamo che sia passato un java.sql.Date
                    any(Time.class)  // Controlliamo che sia passato un java.sql.Time
            );
        }
    }

    @Test
    void testBookAppointment_NoSession() {
        TutorBean tutorBean = new TutorBean();
        LocalDate futureDate = LocalDate.now().plusDays(1);

        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class)) {
            // Simuliamo utente non loggato
            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(null);

            DBException ex = assertThrows(DBException.class, () ->
                    controller.bookAppointment(tutorBean, futureDate, 10)
            );
            assertEquals("Sessione utente non trovata.", ex.getMessage());
        }
    }

    @Test
    void testBookAppointment_NullTutor() {
        // Non serve mockare la sessione perché il controllo sul tutor avviene PRIMA
        DBException ex = assertThrows(DBException.class, () ->
                controller.bookAppointment(null, LocalDate.now(), 10)
        );
        assertEquals("Tutor non selezionato.", ex.getMessage());
    }

    @Test
    void testBookAppointment_PastDate() {
        // Setup: Data passata
        TutorBean tutor = new TutorBean();
        LocalDate pastDate = LocalDate.now().minusDays(1); // Ieri
        UserBean user = new UserBean();

        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class)) {
            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(user);

            DBException ex = assertThrows(DBException.class, () ->
                    controller.bookAppointment(tutor, pastDate, 10)
            );
            assertEquals("Non puoi prenotare una data passata.", ex.getMessage());
        }
    }

    @Test
    void testBookAppointment_InvalidHour() {
        // Setup: Ora invalida (25)
        TutorBean tutor = new TutorBean();
        LocalDate futureDate = LocalDate.now().plusDays(1);
        UserBean user = new UserBean();

        try (MockedStatic<LoginSessionManager> sessionMock = mockStatic(LoginSessionManager.class)) {
            sessionMock.when(LoginSessionManager::loadUserSession).thenReturn(user);

            DBException ex = assertThrows(DBException.class, () ->
                    controller.bookAppointment(tutor, futureDate, 25)
            );
            assertEquals("Ora non valida (usa un'ora compresa tra 0 e 23).", ex.getMessage());
        }
    }
}