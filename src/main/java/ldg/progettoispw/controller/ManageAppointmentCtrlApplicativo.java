package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.applicativo.LoginSessionManager;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.engineering.dao.AppointmentDAO;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.engineering.gof.state.AppointmentContext;
import ldg.progettoispw.model.Appointment;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final Logger LOGGER = Logger.getLogger(ManageAppointmentCtrlApplicativo.class.getName());
    /**
     * Recupera tutti gli appuntamenti con stato "in_attesa" per il tutor attualmente loggato.
     *
     * @return lista di AppointmentBean in attesa per il tutor loggato
     * @throws DBException           se avviene un errore durante l’accesso al database
     * @throws IllegalStateException se la sessione non è attiva o l’utente non è un tutor
     */
    public List<AppointmentBean> getAppuntamentiInAttesa() throws DBException {
        // 1️⃣ Recupera la sessione utente
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
     *
     * @param bean   il bean ricevuto dal controller grafico
     * @param action tipo di azione: "conferma" oppure "rifiuta"
     * @throws DBException              in caso di errore durante l'aggiornamento del database
     * @throws IllegalArgumentException se l'azione non è valida
     */
    public void handleAppointmentAction(AppointmentBean bean, String action){
        // 🔹 Converte il bean nel model
        Appointment model = new Appointment(
                bean.getId(),
                bean.getStudenteEmail(),
                bean.getTutorEmail(),
                bean.getData(),
                bean.getOra(),
                bean.getStato()
        );

        // 🔹 Crea il contesto (pattern State)
        AppointmentContext context = new AppointmentContext(model);
        try {

            // 🔹 Esegue l’azione richiesta
            switch (action.toLowerCase()) {
                case "conferma" -> context.confirm();
                case "rifiuta" -> context.cancel();
                default -> throw new IllegalArgumentException("Azione non valida: " + action);
            }

        } catch (DBException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE,
                        "Errore nel database: " + e.getMessage(),
                        e);
            }
        }
    }

    /**
     * Metodo helper per confermare un appuntamento.
     *
     * @param bean AppointmentBean dell’appuntamento da confermare
     */
    public void confermaAppuntamento(AppointmentBean bean) {
        handleAppointmentAction(bean, "conferma");
    }

    /**
     * Metodo helper per rifiutare un appuntamento.
     *
     * @param bean AppointmentBean dell’appuntamento da rifiutare
     */
    public void rifiutaAppuntamento(AppointmentBean bean) {
        handleAppointmentAction(bean, "rifiuta");

    }
}