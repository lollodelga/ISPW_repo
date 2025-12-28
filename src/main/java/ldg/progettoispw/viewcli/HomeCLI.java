package ldg.progettoispw.viewcli;

import ldg.progettoispw.controller.HomePageController;
import ldg.progettoispw.util.GControllerHome;

import java.util.logging.Logger;

public abstract class HomeCLI extends BaseCLI implements GControllerHome {

    private static final Logger LOGGER = Logger.getLogger(HomeCLI.class.getName());

    protected final HomePageController controller;
    private String userDataHeader;

    protected HomeCLI() {
        super();
        this.controller = new HomePageController();
    }

    // --- Template Method Hooks ---
    protected abstract String getFixedRole();
    protected abstract String getDashboardTitle();
    protected abstract void printMenuOptions();
    protected abstract boolean handleMenuOption(String choice); // Ritorna false se l'opzione non è valida

    @Override
    public void start() {
        boolean running = true;

        while (running) {
            // Parti comuni
            printHeader(getDashboardTitle());
            printUserInfo();

            // Parti specifiche (delegate ai figli)
            printMenuOptions();
            LOGGER.info("0. Logout"); // Il logout è comune a tutti

            String choice = readInput("Scegli un'opzione");

            if (choice.equals("0")) {
                logout();
                running = false;
            } else {
                // Se il figlio non gestisce l'opzione (ritorna false), stampiamo errore
                if (!handleMenuOption(choice)) {
                    showError("Opzione non valida.");
                }
            }
        }
    }

    // ... (Il resto dei metodi updateUserData, printUserInfo, logout rimangono uguali) ...
    @Override
    public void updateUserData(String name, String surname, String birthDate, String subjects) {
        this.userDataHeader = String.format("""
            ------------------------------------------
            Dati Utente:
            Nome: %s %s
            Ruolo: %s
            Nascita: %s
            ------------------------------------------
            """, name, surname, this.getFixedRole(), birthDate);
    }

    protected void printUserInfo() {
        controller.refreshUserData(this);
        LOGGER.info(userDataHeader);
    }

    protected void logout() {
        LOGGER.info("Logout in corso...");
        controller.logout();
    }
}