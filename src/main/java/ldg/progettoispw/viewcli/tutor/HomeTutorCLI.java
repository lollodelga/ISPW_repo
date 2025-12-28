package ldg.progettoispw.viewcli.tutor;

import ldg.progettoispw.viewcli.HomeCLI;

import java.util.logging.Logger;

public class HomeTutorCLI extends HomeCLI {

    private static final Logger LOGGER = Logger.getLogger(HomeTutorCLI.class.getName());

    @Override
    protected String getFixedRole() {
        return "Tutor";
    }

    @Override
    protected String getDashboardTitle() {
        return "DASHBOARD TUTOR";
    }

    @Override
    protected void printMenuOptions() {
        LOGGER.info("1. Recensioni ricevute (e Statistiche)");
        LOGGER.info("2. Gestione Richieste in attesa");
        LOGGER.info("3. Storico Appuntamenti (Concludi/Annulla)");
    }

    @Override
    protected boolean handleMenuOption(String choice) {
        switch (choice) {
            case "1":
                new ManageReviewsCLI().start();
                return true;
            case "2":
                new AppPendTutorCLI().start();
                return true;
            case "3":
                new AppRispostiTutorCLI().start();
                return true;
            default:
                return false; // Opzione non gestita
        }
    }
}