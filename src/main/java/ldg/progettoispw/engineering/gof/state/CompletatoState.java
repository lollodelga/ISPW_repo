package ldg.progettoispw.engineering.gof.state;

public class CompletatoState extends BaseState {
    public CompletatoState() {
        super("completato",
                "Impossibile confermare: appuntamento già completato.",
                "Impossibile annullare: appuntamento già completato.",
                "Appuntamento già completato.");
    }
}