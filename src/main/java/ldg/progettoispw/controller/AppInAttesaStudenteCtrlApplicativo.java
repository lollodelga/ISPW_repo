package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.applicativo.LoginSessionManager;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.engineering.dao.AppointmentDAO;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.engineering.factory.DAOFactory;

import java.util.List;

public class AppInAttesaStudenteCtrlApplicativo {

    private final AppointmentDAO appointmentDAO;

    public AppInAttesaStudenteCtrlApplicativo(){
        this.appointmentDAO = DAOFactory.getAppointmentDAO();
    }

    public List<AppointmentBean> getAppuntamentiInAttesa() throws DBException {
        UserBean user = LoginSessionManager.loadUserSession();

        if (user == null) {
            throw new IllegalStateException("Sessione utente non attiva.");
        }

        String studentEmail = user.getEmail();
        if (studentEmail == null || studentEmail.isEmpty()) {
            throw new IllegalStateException("Email dello studente non trovata nella sessione.");
        }

        return appointmentDAO.getAppuntamentiInAttesa(studentEmail, false);
    }
}