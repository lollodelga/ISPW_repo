package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.applicativo.LoginSessionManager;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.engineering.dao.AppointmentDAO;
import ldg.progettoispw.engineering.exception.DBException;

import java.util.List;

public class AppRispostiStudenteCtrlApplicativo {
    private final AppointmentDAO dao = new AppointmentDAO();
    /**
     * Restituisce la lista degli appuntamenti confermati/completati/annullati dello studente loggato.
     */
    public List<AppointmentBean> getAppuntamentiStudente() throws DBException {
        UserBean studente = LoginSessionManager.loadUserSession();

        if (studente == null || studente.getEmail() == null || studente.getEmail().isEmpty()) {
            throw new IllegalStateException("Sessione studente non attiva o email mancante.");
        }

        // 0 = studente, come flag per il metodo generico getAppuntamentiByEmail
        return dao.getAppuntamentiByEmail(studente.getEmail(), 0);
    }


}
