package ldg.progettoispw.viewcli;

public class Printer {

    // Codici ANSI per i colori in console
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_CYAN = "\u001B[36m"; // Opzionale, carino per i sottotitoli

    // Costruttore privato per impedire l'istanziazione (è una utility class)
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
        System.out.println(ANSI_RED + "ERRORE: " + message + ANSI_RESET);
    }

    /**
     * Metodo rapido per segnalare una scelta non valida nel menu.
     */
    public static void invalidChoicePrint() {
        System.out.println(ANSI_RED + "Scelta non valida. Riprova..." + ANSI_RESET);
    }
}