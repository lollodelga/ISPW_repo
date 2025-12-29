package ldg.progettoispw;

import ldg.progettoispw.engineering.factory.DataSynchronizer;
import ldg.progettoispw.engineering.factory.PersistenceConfig;
import ldg.progettoispw.viewcli.FirstPageCLI;
import ldg.progettoispw.viewcli.Printer;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // --- FASE 1: SCELTA PERSISTENZA ---
        Printer.printlnBlu("=================================");
        Printer.printlnBlu("   CONFIGURAZIONE SISTEMA        ");
        Printer.printlnBlu("=================================");
        Printer.println("1. Database (MySQL)");
        Printer.println("2. File System (CSV)");
        Printer.println("3. Modalità DEMO (In Memoria)"); // Nuova opzione
        Printer.print("Scegli modalità salvataggio: ");

        String persChoice = scanner.nextLine();

        switch (persChoice) {
            case "2":
                Printer.println(">> Modalità selezionata: FILE SYSTEM (CSV)");
                PersistenceConfig.getInstance().setType(PersistenceConfig.PersistenceType.CSV);
                break;
            case "3":
                Printer.println(">> Modalità selezionata: DEMO (RAM - Volatile)");
                PersistenceConfig.getInstance().setType(PersistenceConfig.PersistenceType.DEMO);
                break;
            default:
                Printer.println(">> Modalità selezionata: DATABASE (MySQL)");
                PersistenceConfig.getInstance().setType(PersistenceConfig.PersistenceType.JDBC);
                break;
        }

        // --- FASE 2: SINCRONIZZAZIONE AUTOMATICA ---
        // NON sincronizziamo se siamo in DEMO (partiamo puliti o con dati finti)
        if (PersistenceConfig.getInstance().getType() != PersistenceConfig.PersistenceType.DEMO) {
            DataSynchronizer sync = new DataSynchronizer();
            sync.syncData();
        } else {
            Printer.println(">> Sync disabilitato per modalità DEMO.");
        }

        // --- FASE 3: SCELTA INTERFACCIA ---
        Printer.println(""); // Spazio
        Printer.printlnBlu("=================================");
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
            Printer.errorPrint("Scelta non valida. Riavvia il programma.");
        }
    }
}