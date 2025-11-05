package ldg.progettoispw.engineering.gof.state;

import ldg.progettoispw.engineering.exception.DBException;

public class AnnullatoState implements AppointmentState {

    @Override
    public void confirm(AppointmentContext context) throws DBException {
        throw new DBException("Impossibile confermare: appuntamento annullato.");
    }

    @Override
    public void cancel(AppointmentContext context) throws DBException {
        throw new DBException("Appuntamento già annullato.");
    }

    @Override
    public void complete(AppointmentContext context) throws DBException {
        throw new DBException("Impossibile completare un appuntamento annullato.");
    }

    @Override
    public String getName() {
        return "annullato";
    }
}
