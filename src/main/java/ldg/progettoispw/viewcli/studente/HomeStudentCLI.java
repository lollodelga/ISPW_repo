package ldg.progettoispw.viewcli.studente;

import ldg.progettoispw.viewcli.HomeCLI;

import java.util.logging.Logger;

public class HomeStudentCLI extends HomeCLI {

    private static final Logger LOGGER = Logger.getLogger(HomeStudentCLI.class.getName());

    @Override
    protected String getFixedRole() {
        return "Studente";
    }

    @Override
    protected String getDashboardTitle() {
        return "DASHBOARD STUDENTE";
    }

    @Override
    protected void printMenuOptions() {
        LOGGER.info("1. Cerca Tutor e Prenota");
        LOGGER.info("2. Richieste in sospeso");
        LOGGER.info("3. Richieste completate (e Recensioni)");
    }

    @Override
    protected boolean handleMenuOption(String choice) {
        switch (choice) {
            case "1":
                new SearchTutorCLI().start();
                return true;
            case "2":
                new AppInAttesaStudentCLI().start();
                return true;
            case "3":
                new AppRispostiStudentCLI().start();
                return true;
            default:
                return false; // Opzione non gestita
        }
    }
}