package ldg.progettoispw.engineering.gof.state;

import ldg.progettoispw.engineering.dao.AppointmentDAO;
import ldg.progettoispw.engineering.exception.DBException;

import java.sql.Date;
import java.sql.Time;

public class AppointmentContext {
    private String studentEmail;
    private String tutorEmail;
    private Date date;
    private Time time;
    private AppointmentState state;

    /**
     * Gestisce il ciclo di vita di un appuntamento utilizzando il pattern State (GoF).
     *
     * <p>Ogni appuntamento può trovarsi in uno e un solo stato alla volta, rappresentato
     * da un oggetto che implementa l'interfaccia {@link AppointmentState}.
     * Lo stato corrente determina quali operazioni sono consentite e quali vietate,
     * evitando così l'uso di numerosi controlli condizionali basati su stringhe o flag.</p>
     *
     * <p>Gli stati principali del ciclo di vita sono:</p>
     * <ul>
     *   <li><b>InAttesaState</b> – Appuntamento appena richiesto dallo studente, in attesa di conferma da parte del tutor.</li>
     *   <li><b>ConfermatoState</b> – Appuntamento accettato dal tutor, può essere completato o annullato.</li>
     *   <li><b>CompletatoState</b> – Appuntamento terminato con successo, non può più essere modificato.</li>
     *   <li><b>AnnullatoState</b> – Appuntamento annullato da una delle parti, non può più essere riattivato.</li>
     * </ul>
     *
     * <p>La transizione tra stati avviene tramite i metodi definiti nell'interfaccia {@link AppointmentState}:</p>
     * <ul>
     *   <li>{@code confirm()} – conferma l'appuntamento (solo se in stato "in_attesa").</li>
     *   <li>{@code cancel()} – annulla l'appuntamento (se "in_attesa" o "confermato").</li>
     *   <li>{@code complete()} – segna l'appuntamento come completato (solo se "confermato").</li>
     * </ul>
     *
     * <p>Ogni cambiamento di stato richiama internamente {@link #updateStatusInDB(String)}
     * per sincronizzare la modifica con il database (colonna <code>stato</code> della tabella <code>appuntamento</code>).</p>
     *
     * <p>Questo approccio separa la logica di gestione degli stati in classi dedicate,
     * migliorando l’estendibilità e la manutenibilità del codice, e rappresenta
     * un uso corretto e concreto del pattern <b>State</b> nel dominio dell’applicazione.</p>
     *
     * @author [Tuo Nome]
     * @see AppointmentState
     * @see InAttesaState
     * @see ConfermatoState
     * @see CompletatoState
     * @see AnnullatoState
     */


    public AppointmentContext(String studentEmail, String tutorEmail, Date date, Time time, AppointmentState state) {
        this.studentEmail = studentEmail;
        this.tutorEmail = tutorEmail;
        this.date = date;
        this.time = time;
        this.state = state;
    }

    public void setState(AppointmentState state) {
        this.state = state;
    }

    public AppointmentState getState() {
        return state;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getTutorEmail() {
        return tutorEmail;
    }

    public Date getDate() {
        return date;
    }

    public Time getTime() {
        return time;
    }

    // metodi delegati
    public void confirm() throws DBException { state.confirm(this); }
    public void cancel() throws DBException { state.cancel(this); }
    public void complete() throws DBException { state.complete(this); }

    // helper per aggiornare il DB
    public void updateStatusInDB(String newStatus) throws DBException {
        AppointmentDAO dao = new AppointmentDAO();
        dao.updateAppointmentStatus(studentEmail, tutorEmail, date, time, newStatus);
    }
}
