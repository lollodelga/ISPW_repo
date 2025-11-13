package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.applicativo.LoginSessionManager;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.engineering.dao.AppointmentDAO;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.engineering.gof.state.AppointmentContext;
import ldg.progettoispw.model.Appointment;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class AppRispostiTutorCtrlApplicativo {
    private final AppointmentDAO dao = new AppointmentDAO();
    /**
     * Restituisce la lista degli appuntamenti del tutor loggato, esclusi quelli in attesa.
     */
    public List<AppointmentBean> getAppuntamentiTutor() throws DBException {
        UserBean tutor = LoginSessionManager.loadUserSession();

        if (tutor == null || tutor.getEmail() == null || tutor.getEmail().isEmpty()) {
            throw new IllegalStateException("Sessione tutor non attiva o email mancante.");
        }

        // 1 = tutor, usa il metodo generico per prendere confermati/completati/annullati
        return dao.getAppuntamentiByEmail(tutor.getEmail(), 1);
    }

    public void updateAppointmentStatus(String studentEmail, Date date, Time time, String stato, String action) {
        try {
            // 🔹 Recupera la tutor email dalla sessione attiva
            String tutorEmail = LoginSessionManager.loadUserSession().getEmail();

            // 🔹 Crea e popola l’oggetto Appointment
            Appointment appointment = new Appointment();
            appointment.setStudentEmail(studentEmail);
            appointment.setTutorEmail(tutorEmail);
            appointment.setDate(date);
            appointment.setTime(time);
            appointment.setStatus(stato);
            // 🔹 Crea il contesto per il pattern State
            AppointmentContext context = new AppointmentContext(appointment);

            // 🔹 Applica l’azione scelta
            if ("complete".equalsIgnoreCase(action)) {
                context.complete();
            } else if ("cancel".equalsIgnoreCase(action)) {
                context.cancel();
            } else {
                System.err.println("Azione non riconosciuta: " + action);
            }

        } catch (DBException e) {
            System.err.println("Errore nel cambiare stato all'appuntamento: " + e.getMessage());
            e.printStackTrace(); // utile per debugging
        }
    }
}
