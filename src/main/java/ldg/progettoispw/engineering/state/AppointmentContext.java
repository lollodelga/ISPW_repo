package ldg.progettoispw.engineering.state;

import ldg.progettoispw.engineering.dao.AppointmentDAO;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.model.Appointment;
import java.sql.Date;
import java.sql.Time;

public class AppointmentContext {
    private String studentEmail;
    private String tutorEmail;
    private Date date;
    private Time time;
    private AppointmentState state;

    public AppointmentContext(Appointment appointment) {
        this.studentEmail = appointment.getStudenteEmail();
        this.tutorEmail = appointment.getTutorEmail();
        this.date = appointment.getData();
        this.time = appointment.getOra();

        String status = appointment.getStato();
        if (status == null) {
            this.state = new InAttesaState();
        } else {
            switch (status.toLowerCase()) {
                case "confermato" -> this.state = new ConfermatoState();
                case "completato" -> this.state = new CompletatoState();
                case "pagato"     -> this.state = new PagatoState();
                case "annullato"  -> this.state = new AnnullatoState();
                default           -> this.state = new InAttesaState();
            }
        }
    }

    public void setState(AppointmentState state) {
        this.state = state;
    }

    public AppointmentState getState() { return state; }
    public String getStudentEmail() { return studentEmail; }
    public String getTutorEmail() { return tutorEmail; }
    public Date getDate() { return date; }
    public Time getTime() { return time; }

    // METODI DELEGATI
    public void confirm() throws DBException { state.confirm(this); }
    public void cancel() throws DBException { state.cancel(this); }

    // Flusso: complete() -> poi pay()
    public void complete() throws DBException { state.complete(this); }
    public void pay() throws DBException { state.pay(this); }

    public void updateStatusInDB(String newStatus) throws DBException {
        AppointmentDAO dao = new AppointmentDAO();
        dao.updateAppointmentStatus(studentEmail, tutorEmail, date, time, newStatus);
    }
}