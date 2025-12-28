package ldg.progettoispw.viewcli;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseCLI {

    // Logger statico per la classe base
    private static final Logger LOGGER = Logger.getLogger(BaseCLI.class.getName());

    protected final Scanner scanner;

    protected BaseCLI() {
        // Inizializza lo scanner una volta per tutte le sottoclassi
        this.scanner = new Scanner(System.in);
    }

    /**
     * Ogni vista CLI deve avere un metodo start per iniziare l'interazione.
     */
    public abstract void start();

    protected void showError(String message) {
        LOGGER.log(Level.SEVERE, "\n***********************************\nERRORE: {0}\n***********************************", message);
    }

    protected void printHeader(String title) {
        // Qui usiamo String.format esplicitamente perché il formato è complesso,
        // ma per il prompt sotto usiamo il logger direttamente.
        String header = String.format("%n===================================%n   %s%n===================================", title.toUpperCase());
        LOGGER.info(header);
    }

    /**
     * Helper per leggere input stringa puliti.
     */
    protected String readInput(String prompt) {
        // FIX SONARQUBE: Uso LOGGER.log con parametri {0} invece della concatenazione (+)
        // Questo evita di creare nuove stringhe se il logger è disattivato.
        LOGGER.log(Level.INFO, "{0}: ", prompt);
        return scanner.nextLine().trim();
    }
}