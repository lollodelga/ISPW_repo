package ldg.progettoispw.viewcli;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseCLI {

    // Logger statico per la classe base
    private static final Logger LOGGER = Logger.getLogger(BaseCLI.class.getName());

    protected final Scanner scanner;

    // FIX SONARQUBE: Il costruttore di una classe abstract deve essere protected
    protected BaseCLI() {
        // Inizializza lo scanner una volta per tutte le sottoclassi
        this.scanner = new Scanner(System.in);
    }

    /**
     * Ogni vista CLI deve avere un metodo start per iniziare l'interazione.
     */
    public abstract void start();

    /**
     * Equivalente di showWarning() della GUI.
     * FIX SONARQUBE: Uso LOGGER.log(Level.SEVERE, ...) invece di System.out
     */
    protected void showError(String message) {
        // Uso {0} per i parametri nel logger (best practice)
        LOGGER.log(Level.SEVERE, "\n***********************************\nERRORE: {0}\n***********************************", message);
    }

    /**
     * Helper per stampare intestazioni uniformi.
     * FIX SONARQUBE: Uso LOGGER.info(...)
     */
    protected void printHeader(String title) {
        String header = String.format("%n===================================%n   %s%n===================================", title.toUpperCase());
        LOGGER.info(header);
    }

    /**
     * Helper per leggere input stringa puliti.
     * FIX SONARQUBE: Uso LOGGER.info(...) invece di System.out.print
     */
    protected String readInput(String prompt) {
        // Nota: Il Logger aggiunge un newline, quindi l'input sarà nella riga successiva
        LOGGER.info(prompt + ": ");
        return scanner.nextLine().trim();
    }
}