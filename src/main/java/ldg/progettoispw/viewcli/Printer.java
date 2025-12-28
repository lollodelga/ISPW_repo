package ldg.progettoispw.viewcli;

// Questa annotazione zittisce l'errore "Replace this use of System.out by a logger"
// specificamente per questa classe.
@SuppressWarnings("java:S106")
public class Printer {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_CYAN = "\u001B[36m";

    private Printer() {}

    /**
     * Stampa un messaggio senza andare a capo (utile per gli input).
     */
    public static void print(String message) {
        System.out.print(message);
    }

    /**
     * Stampa un messaggio normale e va a capo.
     */
    public static void println(String message) {
        System.out.println(message);
    }

    /**
     * Stampa un'intestazione o un messaggio in BLU.
     */
    public static void printlnBlu(String message) {
        System.out.println(ANSI_BLUE + message + ANSI_RESET);
    }

    /**
     * Stampa un messaggio di errore in ROSSO.
     */
    public static void errorPrint(String message) {
        // Nota: System.err sarebbe tecnicamente più corretto per gli errori,
        // ma System.out va bene uguale per mantenere l'ordine di stampa in console.
        System.out.println(ANSI_RED + "ERRORE: " + message + ANSI_RESET);
    }

    /**
     * Metodo rapido per segnalare una scelta non valida nel menu.
     */
    public static void invalidChoicePrint() {
        System.out.println(ANSI_RED + "Scelta non valida. Riprova..." + ANSI_RESET);
    }
}