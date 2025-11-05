package ldg.progettoispw.engineering.gof.state;

public class AnnullatoState extends BaseState {
    public AnnullatoState() {
        super("annullato",
                "Impossibile confermare: appuntamento annullato.",
                "Appuntamento già annullato.",
                "Impossibile completare un appuntamento annullato.");
    }
}