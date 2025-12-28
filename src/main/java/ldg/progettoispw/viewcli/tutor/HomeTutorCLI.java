package ldg.progettoispw.viewcli.tutor;

import ldg.progettoispw.viewcli.HomeCLI;

import java.util.logging.Logger;

public class HomeTutorCLI extends HomeCLI {

    // Logger per questa classe
    private static final Logger LOGGER = Logger.getLogger(HomeTutorCLI.class.getName());

    @Override
    protected String getFixedRole() {
        return "Tutor";
    }

    @Override
    public void start() {
        boolean running = true;

        while (running) {
            printHeader("DASHBOARD TUTOR");
            printUserInfo();

            // FIX SONARQUBE: Sostituiti System.out con LOGGER.info
            LOGGER.info("1. Recensioni ricevute (e Statistiche)");
            LOGGER.info("2. Gestione Richieste in attesa");
            LOGGER.info("3. Storico Appuntamenti (Concludi/Annulla)");
            LOGGER.info("0. Logout");

            String choice = readInput("Scegli un'opzione");

            switch (choice) {
                case "1":
                    // Assicurati che anche ManageReviewsCLI sia nel package corretto
                    new ManageReviewsCLI().start();
                    break;
                case "2":
                    new AppPendTutorCLI().start();
                    break;
                case "3":
                    new AppRispostiTutorCLI().start();
                    break;
                case "0":
                    logout();
                    running = false;
                    break;
                default:
                    showError("Opzione non valida.");
            }
        }
    }
}