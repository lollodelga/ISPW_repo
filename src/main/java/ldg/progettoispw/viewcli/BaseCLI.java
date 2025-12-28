package ldg.progettoispw.viewcli;

import java.util.Scanner;

public abstract class BaseCLI {

    protected final Scanner scanner;

    protected BaseCLI() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Ogni vista CLI deve avere un metodo start per iniziare l'interazione.
     */
    public abstract void start();

    protected void showError(String message) {
        Printer.errorPrint(message);
    }

    protected void printHeader(String title) {
        Printer.printlnBlu("\n===================================");
        Printer.printlnBlu("   " + title.toUpperCase());
        Printer.printlnBlu("===================================");
    }

    /**
     * Helper per leggere input stringa puliti.
     */
    protected String readInput(String prompt) {
        Printer.print(prompt + ": ");
        return scanner.nextLine().trim();
    }
}