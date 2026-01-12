package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.api.SentimentAdapter;
import ldg.progettoispw.engineering.api.SentimentAnalyzer;
import ldg.progettoispw.engineering.applicativo.LoginSessionManager;
import ldg.progettoispw.engineering.applicativo.ReviewFilter;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.bean.RecensioneBean;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.engineering.dao.AppointmentDAO;
import ldg.progettoispw.engineering.dao.RecensioneDAO;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.engineering.factory.DAOFactory;
import ldg.progettoispw.engineering.state.AppointmentContext;
import ldg.progettoispw.model.Appointment;

import java.util.List;

/**
 * Controller Applicativo per la gestione dello storico lezioni lato Studente.
 * Gestisce il recupero dati, il pagamento (State Pattern) e le recensioni.
 */
public class AppRispostiStudenteCtrlApplicativo {

    private final ReviewFilter reviewChecker = new ReviewFilter();
    private final SentimentAnalyzer sentimentAnalyzer = new SentimentAdapter();

    private final RecensioneDAO recensioneDAO;
    private final AppointmentDAO appointmentDAO;

    public AppRispostiStudenteCtrlApplicativo() {
        this.recensioneDAO = DAOFactory.getRecensioneDAO();
        this.appointmentDAO = DAOFactory.getAppointmentDAO();
    }

    /**
     * Recupera la lista delle lezioni (completate, pagate, ecc.) per lo studente loggato.
     */
    public List<AppointmentBean> getAppuntamentiStudente() throws DBException {
        UserBean studente = LoginSessionManager.loadUserSession();
        if (studente == null || studente.getEmail() == null) {
            throw new IllegalStateException("Sessione studente non attiva.");
        }

        // 0 indica che vogliamo tutti gli appuntamenti validi per lo storico
        return appointmentDAO.getAppuntamentiByEmail(studente.getEmail(), 0);
    }

    /**
     * Gestisce il pagamento disaccoppiando la View dal Model.
     * 1. Converte AppointmentBean (Dati UI) in Appointment (Model).
     * 2. Usa AppointmentContext (Pattern State) per eseguire la transizione 'pay()'.
     */
    public void pagaAppuntamento(AppointmentBean bean) throws DBException {
        // 1. Conversione Bean -> Model
        Appointment model = new Appointment();

        model.setId(bean.getId());
        model.setStudentEmail(bean.getStudenteEmail());
        model.setTutorEmail(bean.getTutorEmail());
        model.setDate(bean.getData());
        model.setTime(bean.getOra());
        model.setStatus(bean.getStato());

        // 2. Creazione Context (Pattern State)
        AppointmentContext context = new AppointmentContext(model);

        // 3. Esecuzione azione (transizione Completato -> Pagato)
        context.pay();
    }

    /**
     * Gestisce l'invio della recensione.
     * Verifica il contenuto, calcola il sentiment e salva tramite DAO.
     */
    public String inviaRecensione(RecensioneBean bean) {
        // 1. Controllo Filtro Parolacce
        if (reviewChecker.containsBadWords(bean.getRecensione())) {
            return "Errore: linguaggio non appropriato rilevato nel testo.";
        }

        try {
            // 2. Calcolo Sentiment Score (AI Adapter)
            int score = sentimentAnalyzer.analyze(bean.getRecensione());
            bean.setSentimentValue(score);

            // 3. Persistenza
            recensioneDAO.insertRecensione(bean);

            return "Recensione inviata correttamente.";

        } catch (Exception e) {
            return "Errore di sistema durante l'invio: " + e.getMessage();
        }
    }
}