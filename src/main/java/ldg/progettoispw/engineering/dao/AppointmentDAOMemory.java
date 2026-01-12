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

    // COSTANTE per risolvere il Code Smell "Duplicate literal"
    private static final String STATUS_RICHIESTO = "RICHIESTO";

    // --- BLOCCO STATICO: Dati di prova per la Demo ---
    static {
        AppointmentBean demoAppt = new AppointmentBean();
        demoAppt.setId(1);
        demoAppt.setStudenteEmail("studente@demo.it");
        demoAppt.setTutorEmail("tutor@demo.it");
        demoAppt.setData(Date.valueOf("2025-10-10")); // Data futura
        demoAppt.setOra(Time.valueOf("15:00:00"));
        demoAppt.setStato(STATUS_RICHIESTO); // Uso la costante
        fakeDB.add(demoAppt);

        // Rimosso System.out per pulizia CLI
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
        bean.setStato(STATUS_RICHIESTO); // Uso la costante

        fakeDB.add(bean);
        // Rimosso System.out per pulizia CLI
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
                // Rimosso System.out per pulizia CLI
                return;
            }
        }
        // Rimosso System.out per pulizia CLI
    }

    @Override
    public List<AppointmentBean> getAppuntamentiInAttesa(String email, boolean isTutor) throws DBException {
        List<AppointmentBean> result = new ArrayList<>();
        for (AppointmentBean b : fakeDB) {
            // Logica flessibile sugli stati "In Attesa"
            boolean isInAttesa = STATUS_RICHIESTO.equalsIgnoreCase(b.getStato()) || "IN_ATTESA".equalsIgnoreCase(b.getStato());

            if (isInAttesa) {
                // Risolto Code Smell: "This branch's code block is the same..."
                // Unisco le condizioni con OR logico
                boolean matchTutor = isTutor && b.getTutorEmail().equals(email);
                boolean matchStudent = !isTutor && b.getStudenteEmail().equals(email);

                if (matchTutor || matchStudent) {
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
            // Risolto Code Smell: "Merge this if statement with the enclosing one"
            // E risolto Code Smell duplicazione blocco

            // tipo 0 = Studente, tipo 1 = Tutor
            boolean matchStudent = (tipo == 0 && b.getStudenteEmail().equals(email));
            boolean matchTutor = (tipo == 1 && b.getTutorEmail().equals(email));

            if (matchStudent || matchTutor) {
                result.add(b);
            }
        }
        return result;
    }
}