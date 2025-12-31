package ldg.progettoispw.engineering.state;

public class AnnullatoState extends BaseState {

    public AnnullatoState() {
        // Passiamo solo il nome dello stato.
        // BaseState si occuperà di lanciare eccezioni per qualsiasi azione
        // (confirm, cancel, pay, complete) provata in questo stato.
        super("annullato");
    }

    // Nessun metodo da sovrascrivere: è un vicolo cieco (stato finale).
}