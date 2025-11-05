package ldg.progettoispw.engineering.gof.state;

public class CompletatoState extends BaseState {
    @Override protected String getStateName() { return "completato"; }
    @Override protected String getConfirmMessage() { return "Impossibile confermare: appuntamento già completato."; }
    @Override protected String getCancelMessage() { return "Impossibile annullare: appuntamento già completato."; }
    @Override protected String getCompleteMessage() { return "Appuntamento già completato."; }
}
