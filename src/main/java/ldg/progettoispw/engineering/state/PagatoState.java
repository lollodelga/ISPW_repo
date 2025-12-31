package ldg.progettoispw.engineering.state;

public class PagatoState extends BaseState {

    public PagatoState() {
        super("pagato");
    }

    // Nessuna transizione possibile (è lo stato finale).
    // confirm, cancel, complete, pay lanciano tutti eccezione da BaseState.

    // NOTA: Nella tua GUI (JavaFX), controllerai:
    // if (state.getName().equals("pagato")) { mostraPulsanteRecensione(); }
}