package ldg.progettoispw.viewcli;

import ldg.progettoispw.controller.HomePageController;
import ldg.progettoispw.util.GControllerHome;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class HomeCLI extends BaseCLI implements GControllerHome {

    private static final Logger LOGGER = Logger.getLogger(HomeCLI.class.getName());

    protected final HomePageController controller;
    private String userDataHeader;

    // LinkedHashMap mantiene l'ordine di inserimento
    private final Map<String, MenuEntry> menuOptions = new LinkedHashMap<>();

    protected HomeCLI() {
        super();
        this.controller = new HomePageController();
        setupMenu();
    }

    protected abstract String getFixedRole();
    protected abstract String getDashboardTitle();
    protected abstract void setupMenu();

    protected void addMenuOption(String key, String description, Runnable action) {
        menuOptions.put(key, new MenuEntry(description, action));
    }

    @Override
    public void start() {
        boolean running = true;

        while (running) {
            printHeader(getDashboardTitle());
            printUserInfo();

            // Stampa le opzioni dalla Mappa
            for (Map.Entry<String, MenuEntry> entry : menuOptions.entrySet()) {
                // FIX SONARQUBE: Uso log(Level, msg, params) invece della concatenazione (+)
                LOGGER.log(Level.INFO, "{0}. {1}", new Object[]{entry.getKey(), entry.getValue().description});
            }
            LOGGER.info("0. Logout");

            String choice = readInput("Scegli un'opzione");

            if (choice.equals("0")) {
                logout();
                running = false;
            } else if (menuOptions.containsKey(choice)) {
                // Esegue l'azione associata
                menuOptions.get(choice).action.run();
            } else {
                showError("Opzione non valida.");
            }
        }
    }

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

    private static class MenuEntry {
        String description;
        Runnable action;

        public MenuEntry(String description, Runnable action) {
            this.description = description;
            this.action = action;
        }
    }
}