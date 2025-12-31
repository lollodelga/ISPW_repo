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

    // NON SERVE implementare complete() o pay():
    // BaseState lancia già l'eccezione corretta ("Operazione non consentita in stato in_attesa").

    // NON SERVE implementare getName():
    // BaseState lo gestisce già.
}