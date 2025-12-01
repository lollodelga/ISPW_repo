package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.api.SentimentAdapter;
import ldg.progettoispw.engineering.api.SentimentAnalyzer;
import ldg.progettoispw.engineering.api.SentimentClient;
import ldg.progettoispw.engineering.applicativo.LoginSessionManager;
import ldg.progettoispw.engineering.applicativo.ReviewFilter;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.bean.RecensioneBean;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.engineering.dao.AppointmentDAO;
import ldg.progettoispw.engineering.dao.RecensioneDAO;
import ldg.progettoispw.engineering.exception.DBException;

import java.util.List;

public class AppRispostiStudenteCtrlApplicativo {

    private final ReviewFilter reviewChecker = new ReviewFilter();
    private final SentimentAnalyzer sentimentAnalyzer = new SentimentAdapter(new SentimentClient());
    private final RecensioneDAO recensioneDAO = new RecensioneDAO();

    /**
     * Restituisce la lista degli appuntamenti confermati/completati/annullati dello studente loggato.
     */
    public List<AppointmentBean> getAppuntamentiStudente() throws DBException {
        UserBean studente = LoginSessionManager.loadUserSession();

        if (studente == null || studente.getEmail() == null || studente.getEmail().isEmpty()) {
            throw new IllegalStateException("Sessione studente non attiva o email mancante.");
        }

        AppointmentDAO dao = new AppointmentDAO();
        return dao.getAppuntamentiByEmail(studente.getEmail(), 0);
    }

    /**
     * Invia una recensione:
     * 1) controlla parolacce
     * 2) calcola il sentiment tramite adapter
     * 3) salva nel DB
     */
    public String inviaRecensione(RecensioneBean bean) {
        String testo = bean.getRecensione();

        // 1. Controllo parolacce
        if (reviewChecker.containsBadWords(testo)) {
            return "Errore: la recensione contiene parole non consentite.";
        }

        try {
            // 2. Calcolo sentiment tramite Adapter
            int valoreSentiment = sentimentAnalyzer.analyze(testo);
            bean.setSentimentValue(valoreSentiment);

            // 3. Salvataggio
            recensioneDAO.insertRecensione(bean);

            return "Recensione inviata correttamente.";

        } catch (Exception e) {
            return "Errore durante l'analisi della recensione: " + e.getMessage();
        }
    }
}
