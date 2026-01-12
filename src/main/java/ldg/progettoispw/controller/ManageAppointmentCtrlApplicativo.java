package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.applicativo.LoginSessionManager;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.engineering.dao.AppointmentDAO;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.engineering.state.AppointmentContext;
import ldg.progettoispw.model.Appointment;

import java.util.List;

/**
 * Controller applicativo per la gestione degli appuntamenti dei tutor.
 *
 * Flusso principale:
 * 1. Recupera la sessione utente tramite LoginSessionManager.
 * 2. Verifica che l’utente loggato sia un tutor.
 * 3. Usa AppointmentDAO per ottenere o modificare gli appuntamenti associati.
 *
 * Attualmente, questo controller fornisce un metodo per ottenere tutti gli appuntamenti
 * in attesa di conferma per il tutor loggato.
 */
public class ManageAppointmentCtrlApplicativo {

    public ManageAppointmentCtrlApplicativo() {
        //lo tengo vuoto, perché voglio sia istanziato senza fare nulla
    }

    /**
     * Recupera tutti gli appuntamenti con stato "in_attesa" per il tutor attualmente loggato.
     */
    public List<AppointmentBean> getAppuntamentiInAttesa() throws DBException {
        // Recupera la sessione utente
        UserBean user = LoginSessionManager.loadUserSession();

        if (user == null) {
            throw new IllegalStateException("Sessione utente non attiva.");
        }

        String tutorEmail = user.getEmail();
        if (tutorEmail == null || tutorEmail.isEmpty()) {
            throw new IllegalStateException("Email del tutor non trovata nella sessione.");
        }

        AppointmentDAO dao = new AppointmentDAO();
        return dao.getAppuntamentiInAttesa(tutorEmail, true);
    }

    /**
     * Gestisce un'azione generica (conferma o rifiuta) sull'appuntamento.
     */
    public void handleAppointmentAction(AppointmentBean bean, String action) throws DBException {
        // Converte il bean nel model
        Appointment model = new Appointment(
                bean.getId(),
                bean.getStudenteEmail(),
                bean.getTutorEmail(),
                bean.getData(),
                bean.getOra(),
                bean.getStato()
        );

        // Crea il contesto (pattern State)
        AppointmentContext context = new AppointmentContext(model);

        // Esegue l’azione richiesta
        switch (action.toLowerCase()) {
            case "conferma" -> context.confirm();
            case "rifiuta" -> context.cancel();
            default -> throw new IllegalArgumentException("Azione non valida: " + action);
        }
    }

    /**
     * Metodo helper per confermare un appuntamento.
     */
    public void confermaAppuntamento(AppointmentBean bean) throws DBException {
        handleAppointmentAction(bean, "conferma");
    }

    /**
     * Metodo helper per rifiutare un appuntamento.
     */
    public void rifiutaAppuntamento(AppointmentBean bean) throws DBException {
        handleAppointmentAction(bean, "rifiuta");
    }
}