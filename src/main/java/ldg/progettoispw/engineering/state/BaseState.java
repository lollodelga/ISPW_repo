package ldg.progettoispw.engineering.state;

import ldg.progettoispw.engineering.exception.DBException;

public abstract class BaseState implements AppointmentState {

    private final String stateName;

    protected BaseState(String stateName) {
        this.stateName = stateName;
    }

    @Override
    public void confirm(AppointmentContext context) throws DBException {
        throw new DBException("Operazione 'Conferma' non consentita nello stato: " + stateName);
    }

    @Override
    public void cancel(AppointmentContext context) throws DBException {
        throw new DBException("Operazione 'Annulla' non consentita nello stato: " + stateName);
    }

    @Override
    public void complete(AppointmentContext context) throws DBException {
        throw new DBException("Operazione 'Completa' non consentita nello stato: " + stateName);
    }

    // NUOVO COMPORTAMENTO DI DEFAULT
    @Override
    public void pay(AppointmentContext context) throws DBException {
        throw new DBException("Operazione 'Paga' non consentita nello stato: " + stateName);
    }

    @Override
    public String getName() {
        return stateName;
    }
}