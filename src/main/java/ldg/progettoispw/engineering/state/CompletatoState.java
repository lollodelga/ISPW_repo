package ldg.progettoispw.engineering.state;

import ldg.progettoispw.engineering.exception.DBException;

public class CompletatoState extends BaseState {

    public CompletatoState() {
        super("completato");
    }

    @Override
    public void pay(AppointmentContext context) throws DBException {
        // Transizione: Completato -> Pagato
        context.updateStatusInDB("pagato");
        context.setState(new PagatoState());
    }
}