package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.api.SentimentAdapter;
import ldg.progettoispw.engineering.api.SentimentAnalyzer;
import ldg.progettoispw.engineering.applicativo.LoginSessionManager;
import ldg.progettoispw.engineering.applicativo.ReviewFilter;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.bean.RecensioneBean;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.engineering.dao.AppointmentDAO;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.engineering.factory.DAOFactory;
import ldg.progettoispw.engineering.state.AppointmentContext;
import ldg.progettoispw.model.Appointment;
import ldg.progettoispw.util.RecensioneDAO;

import java.util.List;

/**
 * Controller Applicativo per la gestione dello storico lezioni lato Studente.
 * Gestisce il recupero dati, il pagamento (State Pattern) e le recensioni.
 */
public class AppRispostiStudenteCtrlApplicativo {

    private final ReviewFilter reviewChecker = new ReviewFilter();

    // L'Adapter nasconde completamente il Client.
    // Il Controller vede solo il Target (SentimentAnalyzer).
    private final SentimentAnalyzer sentimentAnalyzer = new SentimentAdapter();

    private final RecensioneDAO recensioneDAO = DAOFactory.getRecensioneDAO();

    public AppRispostiStudenteCtrlApplicativo() {
        // Costruttore vuoto intenzionale
    }

    /**
     * Recupera la lista delle lezioni (completate, pagate, ecc.) per lo studente loggato.
     */
    public List<AppointmentBean> getAppuntamentiStudente() throws DBException {
        UserBean studente = LoginSessionManager.loadUserSession();
        if (studente == null || studente.getEmail() == null) {
            throw new IllegalStateException("Sessione studente non attiva.");
        }
        AppointmentDAO dao = new AppointmentDAO();
        // 0 indica che vogliamo tutti gli appuntamenti validi per lo storico
        return dao.getAppuntamentiByEmail(studente.getEmail(), 0);
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
        model.setStudenteEmail(bean.getStudenteEmail());
        model.setTutorEmail(bean.getTutorEmail());
        model.setData(bean.getData());
        model.setOra(bean.getOra());
        model.setStato(bean.getStato());

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
            // Il controller chiama 'analyze' senza sapere che sotto c'è JSON, Python o HTTP.
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