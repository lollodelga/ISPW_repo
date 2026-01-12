package ldg.progettoispw.engineering.dao;

import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.exception.DBException;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public interface AppointmentDAO {

    void insertAppointment(String studentEmail, String tutorEmail, Date date, Time time) throws DBException;

    boolean isTutorAvailable(String tutorEmail, Date date, Time time) throws DBException;

    void updateAppointmentStatus(String studentEmail, String tutorEmail, Date date, Time time, String newStatus) throws DBException;

    List<AppointmentBean> getAppuntamentiInAttesa(String email, boolean isTutor) throws DBException;

    List<AppointmentBean> getAppuntamentiByEmail(String email, int tipo) throws DBException;
}