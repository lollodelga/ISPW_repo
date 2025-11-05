package ldg.progettoispw.engineering.gof.state;

import ldg.progettoispw.engineering.exception.DBException;

public abstract class BaseState implements AppointmentState {

    private final String stateName;
    private final String confirmMessage;
    private final String cancelMessage;
    private final String completeMessage;

    protected BaseState(String stateName, String confirmMessage, String cancelMessage, String completeMessage) {
        this.stateName = stateName;
        this.confirmMessage = confirmMessage;
        this.cancelMessage = cancelMessage;
        this.completeMessage = completeMessage;
    }

    @Override
    public void confirm(AppointmentContext context) throws DBException {
        throw new DBException(confirmMessage);
    }

    @Override
    public void cancel(AppointmentContext context) throws DBException {
        throw new DBException(cancelMessage);
    }

    @Override
    public void complete(AppointmentContext context) throws DBException {
        throw new DBException(completeMessage);
    }

    @Override
    public String getName() {
        return stateName;
    }
}

