package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.bean.SubjectBean;
import ldg.progettoispw.engineering.bean.TutorBean;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.engineering.dao.AppointmentDAO;
import ldg.progettoispw.engineering.dao.TutorSearchDAO;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.engineering.applicativo.LoginSessionManager;
import ldg.progettoispw.model.Tutor;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BookAppointmentCtrlApplicativo {

    private TutorSearchDAO tutorDAO = new TutorSearchDAO();
    private AppointmentDAO dao = new AppointmentDAO();


    public List<TutorBean> searchTutorBySubject(SubjectBean subjectBean) throws DBException {
        // Recupera i model dal DAO
        List<Tutor> tutorModels = tutorDAO.findTutorsBySubject(subjectBean.getMateria());

        // Converte in Bean per la view
        List<TutorBean> tutorBeans = new ArrayList<>();
        for (Tutor model : tutorModels) {
            TutorBean bean = new TutorBean();
            bean.setEmail(model.getEmail());
            bean.setNome(model.getNome());
            bean.setCognome(model.getCognome());
            bean.setMaterie(model.getMaterie());
            tutorBeans.add(bean);
        }

        return tutorBeans;
    }



    public void bookAppointment(TutorBean tutor, LocalDate localDate, int hour) throws DBException {
        // 1. Controlli base
        if (tutor == null)
            throw new DBException("Tutor non selezionato.");

        UserBean student = LoginSessionManager.loadUserSession();
        if (student == null)
            throw new DBException("Sessione utente non trovata.");

        // 2. Validazioni su data e ora
        if (localDate == null)
            throw new DBException("Data non valida.");

        if (localDate.isBefore(LocalDate.now()))
            throw new DBException("Non puoi prenotare una data passata.");

        if (hour < 0 || hour > 23)
            throw new DBException("Ora non valida (usa un'ora compresa tra 0 e 23).");

        // 3. Conversione in formati SQL
        Date sqlDate = Date.valueOf(localDate);
        Time sqlTime = Time.valueOf(LocalTime.of(hour, 0)); // es. 14:00

        // 5. Inserimento nel DB
        dao.insertAppointment(student.getEmail(), tutor.getEmail(), sqlDate, sqlTime);
    }

}