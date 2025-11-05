package ldg.progettoispw.engineering.gof.state;

public class AnnullatoState extends BaseState {
    @Override protected String getStateName() { return "annullato"; }
    @Override protected String getConfirmMessage() { return "Impossibile confermare: appuntamento annullato."; }
    @Override protected String getCancelMessage() { return "Appuntamento già annullato."; }
    @Override protected String getCompleteMessage() { return "Impossibile completare un appuntamento annullato."; }
}