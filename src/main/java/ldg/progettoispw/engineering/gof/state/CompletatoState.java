package ldg.progettoispw.engineering.gof.state;

import ldg.progettoispw.engineering.exception.DBException;

public class CompletatoState implements AppointmentState {

    @Override
    public void confirm(AppointmentContext context) throws DBException {
        throw new DBException("Impossibile confermare: appuntamento già completato.");
    }

    @Override
    public void cancel(AppointmentContext context) throws DBException {
        throw new DBException("Impossibile annullare: appuntamento già completato.");
    }

    @Override
    public void complete(AppointmentContext context) throws DBException {
        throw new DBException("Appuntamento già completato.");
    }

    @Override
    public String getName() {
        return "completato";
    }
}
