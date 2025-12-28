package ldg.progettoispw;

import ldg.progettoispw.viewcli.FirstPageCLI;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    // FIX SONARQUBE: Definita costante per evitare duplicazione di stringhe letterali
    private static final String SEPARATOR_LINE = "=================================";

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // ------------------------------------------------
        // FASE 1: SCELTA PERSISTENZA (SWAP A FREDDO)
        // ------------------------------------------------
        LOGGER.info(SEPARATOR_LINE);
        LOGGER.info("   CONFIGURAZIONE SISTEMA        ");
        LOGGER.info(SEPARATOR_LINE);
        LOGGER.info("1. Database (MySQL)");
        LOGGER.info("2. File System (CSV)");
        LOGGER.info("3. Modalità DEMO (In Memoria)");
        LOGGER.info("Scegli la modalità di salvataggio dati: ");

        String persChoice = scanner.nextLine();

        switch (persChoice) {
            case "1":
                // DA PROGETTARE: Qui imposterai il singleton per usare JDBC
                LOGGER.info("Modalità Database selezionata (Logica da implementare).");
                break;
            case "2":
                // DA PROGETTARE: Qui imposterai il singleton per usare CSV
                LOGGER.info("Modalità CSV attivata (Logica da implementare).");
                break;
            case "3":
                // DA PROGETTARE: Qui imposterai il singleton per usare la RAM
                LOGGER.info("Modalità DEMO attivata (Logica da implementare).");
                break;
            default:
                // Default
                LOGGER.info("Scelta non valida o default. Database selezionato (Logica da implementare).");
        }

        // ------------------------------------------------
        // FASE 2: SCELTA INTERFACCIA (GUI vs CLI)
        // ------------------------------------------------
        LOGGER.info("");
        LOGGER.info(SEPARATOR_LINE);
        LOGGER.info("   SELEZIONA INTERFACCIA         ");
        LOGGER.info(SEPARATOR_LINE);
        LOGGER.info("1. Interfaccia Grafica (JavaFX)");
        LOGGER.info("2. Riga di Comando (CLI)");
        LOGGER.info("Inserisci scelta (1 o 2): ");

        String viewChoice = scanner.nextLine();

        if (viewChoice.equals("1")) {
            LOGGER.info("Avvio Interfaccia Grafica...");
            MainFXML project = new MainFXML();
            project.run();
        }
        else if (viewChoice.equals("2")) {
            LOGGER.info("Avvio Riga di Comando...");
            FirstPageCLI homeCLI = new FirstPageCLI();
            homeCLI.start();
        }
        else {
            LOGGER.log(Level.WARNING, "Scelta non valida. Riavvia il programma.");
        }
    }
}