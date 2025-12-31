package ldg.progettoispw.engineering.state;

import ldg.progettoispw.engineering.exception.DBException;

public class InAttesaState extends BaseState {

    public InAttesaState() {
        super("in_attesa");
    }

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
}