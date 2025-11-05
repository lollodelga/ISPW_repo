package ldg.progettoispw.engineering.gof.state;

import ldg.progettoispw.engineering.exception.DBException;

public class ConfermatoState implements AppointmentState {

    @Override
    public void confirm(AppointmentContext context) throws DBException {
        throw new DBException("Appuntamento già confermato.");
    }

    @Override
    public void cancel(AppointmentContext context) throws DBException {
        context.updateStatusInDB("annullato");
        context.setState(new AnnullatoState());
    }

    @Override
    public void complete(AppointmentContext context) throws DBException {
        context.updateStatusInDB("completato");
        context.setState(new CompletatoState());
    }

    @Override
    public String getName() {
        return "confermato";
    }
}

