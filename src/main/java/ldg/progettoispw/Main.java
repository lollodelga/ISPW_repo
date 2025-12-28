package ldg.progettoispw;

import ldg.progettoispw.viewcli.FirstPageCLI;
import ldg.progettoispw.viewcli.Printer; // Importa la tua classe dal package corretto

import java.util.Scanner;
import java.util.logging.Logger;

public class Main {

    // Logger mantenuto solo per usi interni (se necessario), ma l'UI usa Printer
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // ------------------------------------------------
        // SCELTA INTERFACCIA (GUI vs CLI)
        // ------------------------------------------------

        // Uso Printer per avere l'output colorato e pulito
        Printer.printlnBlu("================================="); // o .printHeader() se l'hai rinominato
        Printer.printlnBlu("   SELEZIONA INTERFACCIA         ");
        Printer.printlnBlu("=================================");

        Printer.println("1. Interfaccia Grafica (JavaFX)");
        Printer.println("2. Riga di Comando (CLI)");
        Printer.print("Inserisci scelta (1 o 2): ");

        String viewChoice = scanner.nextLine();

        if (viewChoice.equals("1")) {
            Printer.println("Avvio Interfaccia Grafica...");
            MainFXML project = new MainFXML();
            project.run();
        }
        else if (viewChoice.equals("2")) {
            Printer.println("Avvio Riga di Comando...");
            FirstPageCLI homeCLI = new FirstPageCLI();
            homeCLI.start();
        }
        else {
            // Usa il metodo di errore della tua classe Printer per coerenza grafica
            Printer.errorPrint("Scelta non valida. Riavvia il programma.");
        }
    }
}