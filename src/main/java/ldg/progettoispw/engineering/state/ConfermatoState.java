package ldg.progettoispw.engineering.state;

import ldg.progettoispw.engineering.exception.DBException;

public class ConfermatoState extends BaseState {

    public ConfermatoState() {
        super("confermato");
    }

    @Override
    public void complete(AppointmentContext context) throws DBException {
        context.updateStatusInDB("completato");
        context.setState(new CompletatoState());
    }

    @Override
    public void cancel(AppointmentContext context) throws DBException {
        context.updateStatusInDB("annullato");
        context.setState(new AnnullatoState());
    }
}