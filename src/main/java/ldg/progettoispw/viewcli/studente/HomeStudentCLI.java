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
    public void start() {
        boolean running = true;

        while (running) {
            printHeader("DASHBOARD STUDENTE");
            printUserInfo();

            LOGGER.info("1. Cerca Tutor e Prenota");
            LOGGER.info("2. Richieste in sospeso");
            LOGGER.info("3. Richieste completate (e Recensioni)");
            LOGGER.info("0. Logout");

            String choice = readInput("Scegli un'opzione");

            switch (choice) {
                case "1":
                    // Avvia la ricerca tutor
                    new SearchTutorCLI().start();
                    break;
                case "2":
                    // Avvia la visualizzazione richieste in attesa
                    new AppInAttesaStudentCLI().start();
                    break;
                case "3":
                    // Avvia storico e recensioni
                    new AppRispostiStudentCLI().start();
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