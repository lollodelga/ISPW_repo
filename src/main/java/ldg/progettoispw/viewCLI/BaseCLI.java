package ldg.progettoispw.viewCLI;

import java.util.Scanner;

public abstract class BaseCLI {

    protected final Scanner scanner;

    public BaseCLI() {
        // Inizializza lo scanner una volta per tutte le sottoclassi
        this.scanner = new Scanner(System.in);
    }

    /**
     * Ogni vista CLI deve avere un metodo start per iniziare l'interazione.
     */
    public abstract void start();

    /**
     * Equivalente di showWarning() della GUI.
     * Stampa un messaggio di errore formattato in rosso (se la console lo supporta) o con icone.
     */
    protected void showError(String message) {
        System.out.println("\n***********************************");
        System.out.println("ERRORE: " + message);
        System.out.println("***********************************\n");
    }

    /**
     * Helper per stampare intestazioni uniformi.
     */
    protected void printHeader(String title) {
        System.out.println("\n===================================");
        System.out.println("   " + title.toUpperCase());
        System.out.println("===================================");
    }

    /**
     * Helper per leggere input stringa puliti.
     */
    protected String readInput(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }
}