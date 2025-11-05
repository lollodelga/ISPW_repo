package ldg.progettoispw.engineering.gof.state;

import ldg.progettoispw.engineering.exception.DBException;

public abstract class BaseState implements AppointmentState {
    protected abstract String getStateName();

    protected abstract String getConfirmMessage();
    protected abstract String getCancelMessage();
    protected abstract String getCompleteMessage();

    @Override
    public void confirm(AppointmentContext context) throws DBException {
        throw new DBException(getConfirmMessage());
    }

    @Override
    public void cancel(AppointmentContext context) throws DBException {
        throw new DBException(getCancelMessage());
    }

    @Override
    public void complete(AppointmentContext context) throws DBException {
        throw new DBException(getCompleteMessage());
    }

    @Override
    public String getName() {
        return getStateName();
    }
}
