package ldg.progettoispw.engineering.state;

import ldg.progettoispw.engineering.exception.DBException;

public interface AppointmentState {
    void confirm(AppointmentContext context) throws DBException;
    void cancel(AppointmentContext context) throws DBException;
    void complete(AppointmentContext context) throws DBException;
    void pay(AppointmentContext context) throws DBException; // <--- NUOVO METODO
    String getName();
}