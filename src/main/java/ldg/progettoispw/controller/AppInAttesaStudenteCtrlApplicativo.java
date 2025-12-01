package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.applicativo.LoginSessionManager;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.engineering.dao.AppointmentDAO;
import ldg.progettoispw.engineering.exception.DBException;

import java.util.List;

public class AppInAttesaStudenteCtrlApplicativo {

    public AppInAttesaStudenteCtrlApplicativo(){
        //voglio esplicitarlo per avitare che sia inizializzato diversamente
    }

    public List<AppointmentBean> getAppuntamentiInAttesa() throws DBException {
        // 1️⃣ Recupera la sessione utente
        UserBean user = LoginSessionManager.loadUserSession();

        if (user == null) {
            throw new IllegalStateException("Sessione utente non attiva.");
        }

        String studentEmail = user.getEmail();
        if (studentEmail == null || studentEmail.isEmpty()) {
            throw new IllegalStateException("Email del tutor non trovata nella sessione.");
        }

        AppointmentDAO dao = new AppointmentDAO();
        return dao.getAppuntamentiInAttesa(studentEmail, false);
    }
}
