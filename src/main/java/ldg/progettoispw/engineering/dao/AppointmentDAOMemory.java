package ldg.progettoispw.engineering.dao;

import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.exception.DBException;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAOMemory implements AppointmentDAO {

    // Database volatile in RAM
    private static final List<AppointmentBean> fakeDB = new ArrayList<>();

    // --- BLOCCO STATICO: Dati di prova per la Demo ---
    static {
        AppointmentBean demoAppt = new AppointmentBean();
        demoAppt.setId(1);
        demoAppt.setStudenteEmail("studente@demo.it");
        demoAppt.setTutorEmail("tutor@demo.it");
        demoAppt.setData(Date.valueOf("2025-10-10")); // Data futura
        demoAppt.setOra(Time.valueOf("15:00:00"));
        demoAppt.setStato("RICHIESTO"); // In attesa
        fakeDB.add(demoAppt);

        System.out.println("[MEMORY] Appuntamento Demo caricato (Studente -> Tutor)");
    }

    @Override
    public void insertAppointment(String studentEmail, String tutorEmail, Date date, Time time) throws DBException {
        if (!isTutorAvailable(tutorEmail, date, time)) {
            throw new DBException("[DEMO] Il tutor ha già un appuntamento in questa data/ora.");
        }

        AppointmentBean bean = new AppointmentBean();
        bean.setId(fakeDB.size() + 1);
        bean.setStudenteEmail(studentEmail);
        bean.setTutorEmail(tutorEmail);
        bean.setData(date);
        bean.setOra(time);
        bean.setStato("RICHIESTO");

        fakeDB.add(bean);
        System.out.println("[MEMORY] Appuntamento inserito: " + studentEmail + " -> " + tutorEmail);
    }

    @Override
    public boolean isTutorAvailable(String tutorEmail, Date date, Time time) throws DBException {
        for (AppointmentBean b : fakeDB) {
            boolean stessoTutor = b.getTutorEmail().equals(tutorEmail);
            boolean stessaData = b.getData().toString().equals(date.toString());
            boolean stessaOra = b.getOra().toString().equals(time.toString());
            // Controlla anche che non sia stato annullato
            boolean attivo = !b.getStato().equalsIgnoreCase("RIFIUTATO") && !b.getStato().equalsIgnoreCase("ANNULLATO");

            if (stessoTutor && stessaData && stessaOra && attivo) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void updateAppointmentStatus(String studentEmail, String tutorEmail, Date date, Time time, String newStatus) throws DBException {
        for (AppointmentBean b : fakeDB) {
            if (b.getStudenteEmail().equals(studentEmail) &&
                    b.getTutorEmail().equals(tutorEmail) &&
                    b.getData().toString().equals(date.toString()) &&
                    b.getOra().toString().equals(time.toString())) {

                b.setStato(newStatus);
                System.out.println("[MEMORY] Stato aggiornato a: " + newStatus);
                return;
            }
        }
        System.out.println("[MEMORY] Nessun appuntamento trovato da aggiornare.");
    }

    @Override
    public List<AppointmentBean> getAppuntamentiInAttesa(String email, boolean isTutor) throws DBException {
        List<AppointmentBean> result = new ArrayList<>();
        for (AppointmentBean b : fakeDB) {
            // Logica flessibile sugli stati "In Attesa"
            boolean isInAttesa = "RICHIESTO".equalsIgnoreCase(b.getStato()) || "IN_ATTESA".equalsIgnoreCase(b.getStato());

            if (isInAttesa) {
                if (isTutor && b.getTutorEmail().equals(email)) {
                    result.add(b);
                } else if (!isTutor && b.getStudenteEmail().equals(email)) {
                    result.add(b);
                }
            }
        }
        return result;
    }

    @Override
    public List<AppointmentBean> getAppuntamentiByEmail(String email, int tipo) throws DBException {
        List<AppointmentBean> result = new ArrayList<>();
        for (AppointmentBean b : fakeDB) {
            if (tipo == 0) { // Studente
                if (b.getStudenteEmail().equals(email)) result.add(b);
            } else if (tipo == 1) { // Tutor
                // Esclude quelli in attesa per la vista "Agenda" se necessario,
                // oppure ritorna tutto e filtra il controller. Qui ritorno tutto per quel tutor.
                if (b.getTutorEmail().equals(email)) result.add(b);
            }
        }
        return result;
    }
}