package ldg.progettoispw.engineering.gof.state;

import ldg.progettoispw.engineering.exception.DBException;

public class InAttesaState implements AppointmentState {
    @Override
    public void confirm(AppointmentContext context) throws DBException {
        context.updateStatusInDB("confermato");
        context.setState(new ConfermatoState());
    }

    @Override
    public void cancel(AppointmentContext context) throws DBException {
        context.updateStatusInDB("annullato");
        context.setState(new AnnullatoState());
    }

    @Override
    public void complete(AppointmentContext context) throws DBException {
        throw new DBException("Impossibile completare un appuntamento ancora in attesa.");
    }

    @Override
    public String getName() { return "in_attesa"; }
}